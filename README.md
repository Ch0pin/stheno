<p align="center">
  <img src="https://github.com/Ch0pin/stheno/assets/4659186/0c82c3da-1a89-43b5-9b8f-4c161dfc5c5c" alt="Stheno">
</p>

# Stheno

## Overview

**Stheno (Σθεννώ)** is a powerful tool designed for analyzing and manipulating intents in Android applications. Named after the sister of Medusa, Stheno is indeed a sub project of [Medusa](https://github.com/Ch0pin/medusa) that brings formidable capabilities akin to Burp Suite but tailored specifically for intents. This tool is essential for Android penetration testers, developers, and security enthusiasts who seek to understand and secure their applications against intent-based vulnerabilities.

## Features

- **Intent Interception**: Capture and inspect intents sent and received by Android applications. 
- **Intent Modification (TODO)**: Modify intercepted intents to test how applications handle unexpected or malformed data.
- **Intent Replay (TODO)**: Resend captured intents to test the stability and security of applications.
- **Logging and Reporting (TODO)**: Detailed logging of all activities and comprehensive reporting to aid in vulnerability assessment.


## Installation 

Stheno can be used either as a standalone tool or in conjunction with [Medusa](https://github.com/Ch0pin/medusa).

### Standalone Usage:

1. **Install the Requirements:**
   ```sh
   pip install -r requirements.txt
   ```

2. **Build the Project:**
   Navigate to the `Intent-monitor` folder and run:
   ```sh
   ./gradlew build
   ```

### Using with Medusa:

If you are using Stheno with Medusa, only step 2 is necessary:

1. **Build the Project:**
   Navigate to the `Intent-monitor` folder and run:
   ```sh
   ./gradlew build
   ```

---


## Contributing

We welcome contributions from the community! To contribute:

1. Fork the repository.
2. Create a new branch for your feature or bugfix.
3. Implement your changes and test thoroughly.
4. Submit a pull request with a detailed description of your changes.
