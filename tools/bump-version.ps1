param(
  [ValidateSet('patch','minor','major')]
  [string]$level = 'patch',
  [string]$message = "chore(release): bump version",
  [string]$notes = ""
)

$ErrorActionPreference = 'Stop'

$repoRoot = Split-Path -Parent $MyInvocation.MyCommand.Path | Split-Path -Parent
Set-Location $repoRoot

$propsPath = Join-Path $repoRoot 'gradle.properties'
$content = Get-Content $propsPath -Raw
if ($content -notmatch 'mod_version=([0-9]+)\.([0-9]+)\.([0-9]+)') {
  throw "mod_version not found or invalid in gradle.properties"
}
$major = [int]$Matches[1]
$minor = [int]$Matches[2]
$patch = [int]$Matches[3]

switch ($level) {
  'major' { $major++; $minor = 0; $patch = 0 }
  'minor' { $minor++; $patch = 0 }
  'patch' { $patch++ }
}

$newVersion = "$major.$minor.$patch"
$content = $content -replace 'mod_version=[0-9]+\.[0-9]+\.[0-9]+', "mod_version=$newVersion"
Set-Content -Path $propsPath -Value $content -NoNewline

# Update CHANGELOG (prepend simple entry)
$chPath = Join-Path $repoRoot 'CHANGELOG.md'
$today = Get-Date -Format 'yyyy-MM-dd'
$chEntry = "`n## v$newVersion - $today`n$notes`n"
if (Test-Path $chPath) {
  $old = Get-Content $chPath -Raw
  Set-Content -Path $chPath -Value ($old + $chEntry)
} else {
  Set-Content -Path $chPath -Value "# Changelog`n$chEntry"
}

# Commit, tag, push
$gitCmdObj = Get-Command git -ErrorAction SilentlyContinue
if ($gitCmdObj) {
  $gitCmd = $gitCmdObj.Source
} else {
  if (Test-Path 'C:\\Program Files\\Git\\cmd\\git.exe') {
    $gitCmd = 'C:\\Program Files\\Git\\cmd\\git.exe'
  } elseif (Test-Path 'C:\\Program Files\\Git\\bin\\git.exe') {
    $gitCmd = 'C:\\Program Files\\Git\\bin\\git.exe'
  } else {
    throw 'git executable not found in PATH or common install locations.'
  }
}

& $gitCmd add -A
& $gitCmd commit -m $message
& $gitCmd tag "v$newVersion"
& $gitCmd push
& $gitCmd push --tags

Write-Host "Bumped to $newVersion and pushed tag v$newVersion"
