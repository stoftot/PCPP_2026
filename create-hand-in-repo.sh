#!/usr/bin/env bash
set -Eeuo pipefail

# -----------------------------------------------------------------------------
# One-time configuration
#
# SOURCE_HOST and DEST_HOST are intentionally separate. The source can be on
# github.com while the destination is on a GitHub Enterprise instance (or vice
# versa).
# -----------------------------------------------------------------------------
SOURCE_HOST="${SOURCE_HOST:-github.com}"
SOURCE_REPO="${SOURCE_REPO:-stoftot/PCPP_2026}"       # OWNER/REPO

# GHE_HOST is accepted as a backwards-compatible alias for DEST_HOST.
DEST_HOST="${DEST_HOST:-${GHE_HOST:-github.itu.dk}}"
DEST_OWNER="${DEST_OWNER:-PCPP-2026-Gruppe1}"         # user or organization
VISIBILITY="${VISIBILITY:-public}"                    # private | internal | public

# The source is cloned over HTTPS by default. This works without authentication
# for public repositories. For a private source repository, configure Git
# credentials for SOURCE_HOST first (for GitHub hosts, `gh auth setup-git` is
# one option).
SOURCE_URL="https://${SOURCE_HOST}/${SOURCE_REPO}.git"

# -----------------------------------------------------------------------------
# Helpers
# -----------------------------------------------------------------------------
log()  { printf '==> %s\n' "$*"; }
ok()   { printf ' OK %s\n' "$*"; }
warn() { printf 'WARN %s\n' "$*" >&2; }
die()  { printf 'ERROR: %s\n' "$*" >&2; exit 1; }

WORKDIR=""
REMOTE_MAY_EXIST=0
DEST_REPO=""

cleanup() {
    local exit_code=$?

    if [[ -n "${WORKDIR:-}" && -d "$WORKDIR" ]]; then
        rm -rf -- "$WORKDIR" || true
    fi

    if (( exit_code != 0 )) && (( REMOTE_MAY_EXIST == 1 )) && [[ -n "${DEST_REPO:-}" ]]; then
        warn "The temporary local clone was removed, but ${DEST_REPO} may now exist on ${DEST_HOST}."
        warn "Inspect it before retrying. This script intentionally does not auto-delete remote repositories."
    fi
}
trap cleanup EXIT
trap 'exit 130' INT
trap 'exit 143' TERM

require_command() {
    command -v "$1" >/dev/null 2>&1 || die "Required command '$1' is not installed or not on PATH."
}

validate_hostname() {
    local name="$1"
    local value="$2"

    [[ -n "$value" ]] || die "${name} cannot be empty."
    [[ "$value" != *"://"* && "$value" != */* ]] \
        || die "${name} must be a hostname only, e.g. github.com or github.company.com (no https:// and no path)."
}

usage() {
    cat <<USAGE
Usage:
  $(basename "$0") <source-branch> [new-repository-name]

Examples:
  $(basename "$0") release-2026
  $(basename "$0") feature/new-api new-api

If [new-repository-name] is omitted, the branch name is used exactly.
Because '/' is valid in Git branch names but not in a repository name,
a branch such as 'feature/new-api' requires the second argument.

Configuration (environment variables or edit the top of this script):
  SOURCE_HOST    Source GitHub hostname          (current: $SOURCE_HOST)
  SOURCE_REPO    Existing source OWNER/REPO      (current: $SOURCE_REPO)
  DEST_HOST      Destination GitHub hostname     (current: $DEST_HOST)
  DEST_OWNER     Destination user/organization   (current: $DEST_OWNER)
  VISIBILITY     private, internal, or public     (current: $VISIBILITY)

Current transfer:
  https://${SOURCE_HOST}/${SOURCE_REPO}
      ->
  https://${DEST_HOST}/${DEST_OWNER}/<new-repository-name>
USAGE
}

# -----------------------------------------------------------------------------
# Preflight: deliberately runs before validating/using branch arguments.
# It checks machine setup, source access, and destination authentication.
# -----------------------------------------------------------------------------
preflight() {
    log "Running machine, source, and destination preflight checks..."

    require_command git
    require_command gh
    require_command mktemp
    require_command awk

    # A cleanup commit is created later, so Git needs an author identity.
    local git_user_name git_user_email
    git_user_name="$(git config --get user.name 2>/dev/null || true)"
    git_user_email="$(git config --get user.email 2>/dev/null || true)"
    [[ -n "$git_user_name" ]] \
        || die "Git user.name is not configured. Run: git config --global user.name \"Your Name\""
    [[ -n "$git_user_email" ]] \
        || die "Git user.email is not configured. Run: git config --global user.email \"you@example.com\""

    validate_hostname "SOURCE_HOST" "$SOURCE_HOST"
    validate_hostname "DEST_HOST" "$DEST_HOST"

    [[ "$SOURCE_REPO" =~ ^[^/]+/[^/]+$ ]] \
        || die "SOURCE_REPO must have the form OWNER/REPO, not a full URL. Current value: '${SOURCE_REPO}'."

    [[ "$DEST_OWNER" != */* && -n "$DEST_OWNER" ]] \
        || die "DEST_OWNER must be a single GitHub user or organization name."

    case "$VISIBILITY" in
        private|internal|public) ;;
        *) die "VISIBILITY must be one of: private, internal, public." ;;
    esac

    git --version >/dev/null 2>&1 || die "Git is installed but not runnable."
    gh --version  >/dev/null 2>&1 || die "GitHub CLI is installed but not runnable."
    ok "Required local tools are installed"

    # Source access is checked with Git rather than GitHub CLI. This allows a
    # public github.com source to work even if `gh` is authenticated only to the
    # Enterprise destination.
    if ! GIT_TERMINAL_PROMPT=0 git ls-remote "$SOURCE_URL" >/dev/null 2>&1; then
        die "Cannot access source repository ${SOURCE_URL}. Check the URL/network. If the source is private, configure Git credentials for ${SOURCE_HOST}."
    fi
    ok "Source repository ${SOURCE_HOST}/${SOURCE_REPO} is accessible"

    # Only the destination requires GitHub CLI authentication because this is
    # where the script creates a repository.
    if ! gh auth status --hostname "$DEST_HOST" >/dev/null 2>&1; then
        die "GitHub CLI is not authenticated to destination ${DEST_HOST}. Run: gh auth login --hostname ${DEST_HOST}"
    fi
    ok "GitHub CLI authentication for destination ${DEST_HOST}"

    local protocol
    protocol="$(gh config get git_protocol --host "$DEST_HOST" 2>/dev/null || true)"
    case "$protocol" in
        https|ssh) ok "Git protocol for destination ${DEST_HOST}: ${protocol}" ;;
        *) die "No valid Git protocol is configured for ${DEST_HOST}. Run: gh auth login --hostname ${DEST_HOST}" ;;
    esac

    # Verify the authenticated account can at least resolve the destination
    # owner. Actual repository-creation permission is ultimately enforced when
    # the create operation is attempted.
    if ! GH_HOST="$DEST_HOST" gh api "users/${DEST_OWNER}" >/dev/null 2>&1; then
        die "Destination owner '${DEST_OWNER}' could not be resolved on ${DEST_HOST}. Check the organization/user name and your access."
    fi
    ok "Destination owner ${DEST_OWNER} exists on ${DEST_HOST}"

    ok "Preflight checks passed"
}

preflight

# -----------------------------------------------------------------------------
# Arguments (checked only after machine/source/destination preflight succeeds)
# -----------------------------------------------------------------------------
if (( $# < 1 || $# > 2 )); then
    usage
    exit 2
fi

BRANCH="$1"
NEW_REPO_NAME="${2:-$BRANCH}"
DEST_REPO="${DEST_OWNER}/${NEW_REPO_NAME}"

[[ -n "$BRANCH" ]] || die "Source branch cannot be empty."

git check-ref-format --branch "$BRANCH" >/dev/null 2>&1 \
    || die "'${BRANCH}' is not a valid Git branch name."

# Conservative GitHub repository-name validation.
# In particular, '/' is not allowed because it separates OWNER/REPO.
[[ "$NEW_REPO_NAME" =~ ^[A-Za-z0-9._-]+$ ]] \
    || die "Invalid repository name '${NEW_REPO_NAME}'. Use only letters, numbers, '.', '_' or '-'. If the branch contains '/', provide a second argument, e.g. '$0 \"$BRANCH\" \"${BRANCH//\//-}\"'."

[[ "$NEW_REPO_NAME" != "." && "$NEW_REPO_NAME" != ".." ]] \
    || die "Repository name cannot be '.' or '..'."

# Check the requested source branch before creating any temporary clone or
# destination repository.
if ! GIT_TERMINAL_PROMPT=0 git ls-remote --exit-code --heads "$SOURCE_URL" "refs/heads/${BRANCH}" >/dev/null 2>&1; then
    die "Source branch '${BRANCH}' does not exist or is not accessible in ${SOURCE_HOST}/${SOURCE_REPO}. Nothing was changed."
fi
ok "Source branch '${BRANCH}' exists"

if GH_HOST="$DEST_HOST" gh repo view "$DEST_REPO" --json nameWithOwner >/dev/null 2>&1; then
    die "Destination repository ${DEST_REPO} already exists or is already visible to you on ${DEST_HOST}. Nothing was changed."
fi

log "Source:      ${SOURCE_HOST}/${SOURCE_REPO} branch '${BRANCH}'"
log "Destination: ${DEST_HOST}/${DEST_REPO} branch 'main'"
log "Visibility:  ${VISIBILITY}"

# -----------------------------------------------------------------------------
# Temporary clone
# -----------------------------------------------------------------------------
WORKDIR="$(mktemp -d "${TMPDIR:-/tmp}/branch-to-repo.XXXXXXXX")"
REPO_DIR="$WORKDIR/repository"

log "Cloning source branch into temporary directory..."
if ! git clone \
        --branch "$BRANCH" \
        --single-branch \
        --no-tags \
        "$SOURCE_URL" \
        "$REPO_DIR"; then
    die "Clone failed. Verify source access and branch '${BRANCH}'. If the source is private, configure Git credentials for ${SOURCE_HOST}."
fi

cd "$REPO_DIR"

CURRENT_BRANCH="$(git branch --show-current)"
[[ "$CURRENT_BRANCH" == "$BRANCH" ]] \
    || die "Safety check failed: expected checked-out branch '${BRANCH}', got '${CURRENT_BRANCH}'."

SOURCE_SHA="$(git rev-parse HEAD)"
[[ -n "$SOURCE_SHA" ]] || die "Could not determine source commit SHA."
ok "Cloned '${BRANCH}' at ${SOURCE_SHA}"

# Rename selected branch to main. This does not change the commit history.
git branch -M main

[[ "$(git branch --show-current)" == "main" ]] \
    || die "Safety check failed: branch rename to 'main' did not succeed."

[[ "$(git rev-parse HEAD)" == "$SOURCE_SHA" ]] \
    || die "Safety check failed: HEAD changed while renaming the branch."

# -----------------------------------------------------------------------------
# Prepare hand-in contents
#
# Keep only top-level, real directories whose names begin with "Exercise".
# Everything else in the checked-out tree is removed. The .git directory is
# always preserved. We first verify that at least one matching directory exists
# so a typo or unexpected repository layout cannot silently produce an empty
# hand-in repository.
# -----------------------------------------------------------------------------
log "Cleaning temporary copy for hand-in..."

shopt -s dotglob nullglob
exercise_dirs=()
for entry in "$REPO_DIR"/*; do
    name="${entry##*/}"
    [[ "$name" == ".git" ]] && continue

    if [[ -d "$entry" && ! -L "$entry" && "$name" == Exercise* ]]; then
        exercise_dirs+=("$name")
    fi
done

(( ${#exercise_dirs[@]} > 0 )) \
    || die "Safety check failed: no top-level directories beginning with 'Exercise' were found. Nothing will be pushed."

printf '    Keeping:'
printf ' %q' "${exercise_dirs[@]}"
printf '\n'

for entry in "$REPO_DIR"/*; do
    name="${entry##*/}"
    [[ "$name" == ".git" ]] && continue

    if [[ -d "$entry" && ! -L "$entry" && "$name" == Exercise* ]]; then
        continue
    fi

    rm -rf -- "$entry"
done

# Verify the filesystem after deletion before recording the commit.
for entry in "$REPO_DIR"/*; do
    name="${entry##*/}"
    [[ "$name" == ".git" ]] && continue

    if [[ ! -d "$entry" || -L "$entry" || "$name" != Exercise* ]]; then
        die "Safety check failed: unexpected top-level entry remains after cleanup: '${name}'."
    fi
done

git add -A
git commit --allow-empty -m "Cleaning up for handin" >/dev/null
HANDIN_SHA="$(git rev-parse HEAD)"
[[ -n "$HANDIN_SHA" && "$HANDIN_SHA" != "$SOURCE_SHA" ]] \
    || die "Safety check failed: cleanup commit was not created."

PARENT_SHA="$(git rev-parse HEAD^ 2>/dev/null || true)"
[[ "$PARENT_SHA" == "$SOURCE_SHA" ]] \
    || die "Safety check failed: cleanup commit is not directly based on the original source commit."

ok "Created cleanup commit ${HANDIN_SHA}: Cleaning up for handin"

# Remove every source-related remote before any destination is created. This is
# a deliberate guard against accidentally pushing back to the source host.
while IFS= read -r remote; do
    [[ -n "$remote" ]] && git remote remove "$remote"
done < <(git remote)

[[ -z "$(git remote)" ]] \
    || die "Safety check failed: one or more source remotes remain configured."
ok "Source remotes removed from temporary clone"

# -----------------------------------------------------------------------------
# Create destination and push
# -----------------------------------------------------------------------------
case "$VISIBILITY" in
    private)  VISIBILITY_FLAG=(--private) ;;
    internal) VISIBILITY_FLAG=(--internal) ;;
    public)   VISIBILITY_FLAG=(--public) ;;
esac

log "Creating ${DEST_REPO} on ${DEST_HOST} and pushing main..."
if ! GH_HOST="$DEST_HOST" gh repo create "$DEST_REPO" \
        "${VISIBILITY_FLAG[@]}" \
        --source=. \
        --remote=origin \
        --push; then

    # Creation can succeed before a later push fails. Detect that state so the
    # cleanup message does not imply the remote was removed.
    if GH_HOST="$DEST_HOST" gh repo view "$DEST_REPO" --json nameWithOwner >/dev/null 2>&1; then
        REMOTE_MAY_EXIST=1
    fi
    die "Repository creation/push failed."
fi
REMOTE_MAY_EXIST=1

# Explicitly make main the repository's default branch.
if ! GH_HOST="$DEST_HOST" gh repo edit "$DEST_REPO" --default-branch main; then
    die "The repository was created and pushed, but setting 'main' as the default branch failed."
fi

# -----------------------------------------------------------------------------
# Post-push verification
# -----------------------------------------------------------------------------
REMOTE_SHA="$(git ls-remote --exit-code origin refs/heads/main | awk 'NR==1 {print $1}')" \
    || die "Could not verify destination branch 'main'."

[[ "$REMOTE_SHA" == "$HANDIN_SHA" ]] \
    || die "Verification failed: destination main is ${REMOTE_SHA}, expected cleanup commit ${HANDIN_SHA}."

DEFAULT_BRANCH="$(GH_HOST="$DEST_HOST" gh repo view "$DEST_REPO" \
    --json defaultBranchRef --jq '.defaultBranchRef.name')"

[[ "$DEFAULT_BRANCH" == "main" ]] \
    || die "Verification failed: destination default branch is '${DEFAULT_BRANCH}', expected 'main'."

REPO_URL="$(GH_HOST="$DEST_HOST" gh repo view "$DEST_REPO" --json url --jq '.url')"

# All remote operations are confirmed. EXIT trap still removes the temp clone.
REMOTE_MAY_EXIST=0

printf '\nSUCCESS\n'
printf 'Source:         https://%s/%s (branch: %s)\n' "$SOURCE_HOST" "$SOURCE_REPO" "$BRANCH"
printf 'Repository:     %s\n' "$REPO_URL"
printf 'Default branch: main\n'
printf 'Source commit:  %s\n' "$SOURCE_SHA"
printf 'Hand-in commit: %s (Cleaning up for handin)\n' "$REMOTE_SHA"
printf 'Kept folders:  '
printf ' %s' "${exercise_dirs[@]}"
printf '\n'
printf 'Temp clone:     deleted automatically on exit\n'
