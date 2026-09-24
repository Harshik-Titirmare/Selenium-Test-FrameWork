# 🚀 Hybrid Test Automation Framework (Selenium + TestNG + Jenkins + Allure)

An enterprise-ready, robust test automation framework built using **Java**, **Selenium WebDriver**, **TestNG**, and **Maven**. Designed for high scalability, thread-safe parallel execution, detailed interactive reporting, and seamless CI/CD integration with automated notification systems.

---

## 🛠️ Tech Stack & Key Tools
* **Language & Engine:** Java (JDK 17+), Maven
* **UI Automation:** Selenium WebDriver (Headless/Chrome Execution)
* **Test Management:** TestNG (Parallel Execution & Listener Architecture)
* **CI/CD Orchestration:** Jenkins (Automated Scheduling via Cron Expression / Poll SCM)
* **Reporting:** Allure Reports & Extent Reports
* **Notification System:** Jenkins Editable Email Notification Plugin with HTML Templates

---

## ✨ Key Features & Architecture
* **Page Object Model (POM):** Clean separation of UI locators, page actions, and test scripts for maximum maintainability and reusability.
* **Listener-Based Failure Handling:** Custom TestNG listeners (`ITestListener`) intercept test failures in real-time to:
  * Attach failure screenshots directly into interactive Allure Reports.
  * Store timestamped PNG screenshots locally under `target/screenshots/` for CI/CD pipeline access.
* **Automated CI/CD Integration:** 
  * Fully configured Jenkins jobs supporting scheduled runs (Cron) and SCM polling.
  * System-agnostic headless Chrome configuration for smooth headless execution on Linux/Jenkins nodes.
* **Custom HTML Email Reports:**
  * Generates visual HTML email notifications after execution with color-coded success/failure status badges and execution metrics.
  * Automatically attaches failure screenshots from the workspace directly to emails sent to project recipients and stakeholders.

---

## 📊 Sample Execution Workflow
1. **Trigger:** Jenkins initiates the build based on a cron schedule or SCM change.
2. **Execution:** Maven executes TestNG test suites in headless browser mode.
3. **Capture:** Listeners capture thread-safe screenshots and logs on test failures.
4. **Report & Notify:** Allure generates interactive visual reports, and Jenkins sends styled HTML emails with screenshot attachments to specified recipients.
