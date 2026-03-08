# CSB Experience – Small Business Account Opening

A web application that replicates the Cambridge Savings Bank (CSB) new small business account opening flow, built to demonstrate how the experience could be enhanced in the future.

## Overview

This React application walks a small business through a multi-step account opening process mirroring CSB's current flow:

| Step | Description |
|------|-------------|
| 1 | **Get Started** – Purpose selection (new business, existing business, sole proprietor) |
| 2 | **Choose Account** – Select a business account product (Checking, Interest Checking, Savings, Money Market, CD) |
| 3 | **Business Details** – Legal name, entity type, EIN, state of formation, date established, address |
| 4 | **Your Information** – Primary applicant/authorized signer personal info, ID verification |
| 5 | **Beneficial Ownership** – FinCEN CDD Rule compliance – capture all 25%+ owners |
| 6 | **Review & Submit** – Full review with inline editing, disclosures, and e-sign consent |
| 7 | **Confirmation** – Submission confirmation with next steps |

## Tech Stack

- **React 19** + **Vite 7** – Fast modern frontend tooling
- **Tailwind CSS 4** – Utility-first CSS with CSB brand colors
- **@tailwindcss/forms** – Better default form styles

## Getting Started

```bash
cd csb-account-opening
npm install
npm run dev
```

Open [http://localhost:5173](http://localhost:5173) to view the application.

## Building for Production

```bash
npm run build
npm run preview
```

## CSB Brand Colors

| Color | Hex | Usage |
|-------|-----|-------|
| Navy | `#003366` | Primary brand, headers, buttons |
| Green | `#006633` | Accents, success states |
| Gold | `#C8A84B` | Highlights, "Most Popular" badge |

## Customization Notes

- **Add real API calls**: Replace the mock submit in `App.jsx`'s `handleSubmit()` with actual API requests
- **Document upload**: Add a document upload step between step 5 and 6
- **ID verification**: Integrate a KYC/identity verification service (e.g., Alloy, Socure)
- **Account funding**: Add an initial deposit / funding step
- **E-signature**: Integrate a real e-sign flow (DocuSign, HelloSign)
- **CRM integration**: Wire form data to a CRM or core banking system on submission
