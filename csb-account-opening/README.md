# CSB Business Account Opening - ClojureScript

A multi-step business account opening form built with ClojureScript and Reagent.

## Prerequisites

- [Java JDK](https://adoptium.net/) (version 11 or later)
- [Clojure CLI tools](https://clojure.org/guides/install_clojure)
- [Node.js](https://nodejs.org/) (for shadow-cljs and npm packages)

## Setup

Install npm dependencies (for shadow-cljs and Tailwind CSS):

```bash
npm install
```

## Development

Start the shadow-cljs development server:

```bash
# Using npm script
npm run dev

# Or directly with Clojure CLI
clj -M:cljs watch app
```

This will:
- Start a dev server at http://localhost:3000
- Enable hot-reloading for ClojureScript
- Open a REPL for interactive development

## Build for Production

Build an optimized production bundle:

```bash
# Using npm script
npm run build

# Or directly
clj -M:cljs release app
```

The output will be in `resources/public/js/`.

## Project Structure

```
csb-account-opening/
├── deps.edn                    # Clojure dependencies
├── shadow-cljs.edn             # Shadow-cljs configuration
├── package.json                # Node dependencies (Tailwind, etc.)
├── resources/
│   └── public/
│       ├── index.html          # Main HTML file
│       └── css/
│           └── index.css       # Tailwind CSS styles
└── src/
    └── main/
        └── csb/
            ├── core.cljs       # App entry point
            ├── state.cljs      # Application state (Reagent atoms)
            ├── utils.cljs      # Utility functions
            └── components/
                ├── app.cljs    # Main app component
                ├── ui/         # Reusable UI components
                │   ├── header.cljs
                │   ├── progress_sidebar.cljs
                │   └── form_field.cljs
                └── steps/      # Multi-step form components
                    ├── welcome.cljs
                    ├── select_product.cljs
                    ├── business_info.cljs
                    ├── applicant_info.cljs
                    ├── beneficial_owners.cljs
                    ├── review.cljs
                    └── confirmation.cljs
```

## Technologies

- **ClojureScript** - Clojure compiled to JavaScript
- **Reagent** - Minimalistic React wrapper for ClojureScript
- **Shadow-cljs** - ClojureScript build tool with excellent npm integration
- **Tailwind CSS** - Utility-first CSS framework

## REPL Development

Connect to the running shadow-cljs REPL:

```bash
# From another terminal
clj -M:cljs cljs-repl app
```

Or connect your editor (Calva, CIDER, etc.) to the nREPL port shown in the terminal.
