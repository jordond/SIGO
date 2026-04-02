# Security Policy

## Supported Versions

| Version        | Supported          |
|----------------|--------------------|
| Latest release | Yes                |
| Older releases | No                 |

Only the latest released version of SIGO receives security updates. If you are running an older
version, please update before reporting.

## Reporting a Vulnerability

**Please do not open a public issue for security vulnerabilities.**

Instead, report vulnerabilities privately using
[GitHub Security Advisories](https://github.com/jordondehoog/SIGO/security/advisories/new).

When reporting, please include:

- A description of the vulnerability and its potential impact
- Steps to reproduce or a proof of concept
- The affected component (Android app, iOS app, API server, Cloudflare Worker, etc.)
- Any suggested fixes, if you have them

## What to Expect

- **Acknowledgement** within 72 hours of your report.
- We will work with you to understand and validate the issue.
- A fix will be developed privately and released as soon as practical.
- You will be credited in the release notes (unless you prefer otherwise).

## Scope

The following are in scope for security reports:

- The SIGO Android and iOS applications
- The JVM API server (`apps/api/server`)
- The Cloudflare Worker API (`apps/api/worker`)
- The project website

The following are **out of scope**:

- Third-party dependencies (report these to the upstream project)
- The Visual Crossing weather API
- Firebase services

## Best Practices for Contributors

- Never commit secrets, API keys, or credentials. Use `app-env.properties` (gitignored) for local
  config.
- Firebase config files (`google-services.json`, `GoogleService-Info.plist`) must not contain
  production credentials when committed.
- Review the [CONTRIBUTING.md](CONTRIBUTING.md) for setup instructions that keep secrets out of
  version control.
