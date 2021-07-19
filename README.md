# Login testing framework

Login testing framework helps you on the process of automating test cases for login pages

## Table of contents
* Prerequisites
* Installation
* Configuration
* Adding a new test case
* Usage
* Contributing

---

## Prerequisites
- **Java 11 JDK or above**. You can download JDK 11 from [here](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html). After download, you will need to configure your IDE to use this JDK.
  - Configure JDK in [Eclipse](https://www.dummies.com/programming/java/how-to-configure-java-in-eclipse/#:~:text=The%20following%20steps%20show%20you%20how%3A%201%20On,of%20the%20Preferences%20dialog.%20...%20More%20items...%20).
  - Configure JDK in [IntelliJ IDEA](https://www.jetbrains.com/help/idea/sdk.html)
- **Lombok jar** installed in your IDE or text editor. Instructions for installation:
  - [Eclipse](https://projectlombok.org/setup/eclipse).
  - [IntelliJ IDEA](https://projectlombok.org/setup/intellij). For version newer than 2020.3 the Lombok jar is already installed.
- **Maven** for project dependency management. Download and install instructions [here](https://maven.apache.org/). 
  - **NOTE**: Eclipse and IntelliJ already include Maven so in this case you don't need to install this.

---

## Installation

1. Clone the repository. You can do that using Git command `git clone <url of the repository>`. Or if you prefer you can clone it using the VSC functions of 
   - [IntelliJ IDEA](https://www.jetbrains.com/help/idea/set-up-a-git-repository.html) 
   - or [Eclipse](https://www.geeksforgeeks.org/how-to-clone-a-project-from-github-using-eclipse/#:~:text=%20%20%201%20Step%201%3A%20After%20selecting,will%20pop%20up%20in%20which%20yo...%20More%20).

If you used VSC functions of IntelliJ or Eclipse you don't need to perform the next steps as they are performed automatically from the IDE.

2. Import the repository as a Maven project into your preferred IDE or text editor. 
3. Run the Maven installation. This will download all the dependencies of the pom.xml file. `mvn install`

---

## Configuration
For the configuration, copy the content of `src/main/resources/config.properties.example` to `src/main/resources/config.properties`.

In this file you can configure several properties such as: 

- ### env property
    This property will set the workbook sheet from which the program is reading the test data on the `test_data.xlsx`. 

    If your workbook sheet name is `staging`, then you have to set the env property to `staging`.

    You can refer to the `test_data.example.xlsx` included in this repo to have an idea on how the format of the `test_data.xlsx` should be for a correct execution of the test cases.

- ### headless property
    This tells the program if the execution will be in headless mode or not. Set the value to `yes` if you want headless execution. Any other value will turn off the headless execution.

    **NOTE**: headless execution is only available on Chrome. If headless is set to `yes`, and you have IE or Edge tests this will not run on headless.

- ### testData property
    This defines the path to the `test_data.xlsx` file. You can define a different name for this file as long as you set the correct path to the file.

- ### readData property
    This defines the path to the folder in which the results of the execution (screenshots, logs, data sheet) will be placed.

- ### wait property
    This property defines how many seconds will selenium wait for the pages to load.

- ### sendEmail property
    This property defines if an email with test execution results will be sent. Set to `yes` if you want this function or `no` if don't want it.

- ### testSuiteFile property
    This is the file that contains the suite of tests (`testng.xml`). This repo already includes a `testng.xml` so you can use that one.

## Adding a new test case

Here are the steps to add a new test case to this framework

1. Add a row to your test data file. Ensure you follow this format:

   Test Case Id | Test Scenario | Application Name | Url | Browser | Run Flag | User_ID | Password
   -------------|--------------|------------------|-----|---------|----------|---------|---------
   TestGoogle.verifyLogin | Verify login into | Google | https://www.google.come | Chrome | Y | username | password |

   * Valid values for Browser: Chrome, IE, Edge

   * Valid values for Run Flag: Y and N.

   * The Test Scenario column is just a column to describe what is the purpose of the test.

   * For the Application Name column
     
   * For Test Case Id you have to ensure it matches the class name plus the name of the test method.


2. The next step is to add a class to `src/main/java/com/example/testscases`. This class should extend `SetupTestCaseAndTearDown` in order to obtain functionality of reading from data file, take screenshot of the results, etc.
   
   The following code snippet shows an example of how a test class has to be defined to get a correct functionality.
   ```java
   public class TestGoogle extends SetupTestCaseAndTearDown {
   
       @Test
       public void verifyLogin() {
           // define the test case steps
       }
   }
   ```

3. Define your test case steps. The `SetupTestCaseAndTearDown` class provides you a `WebDriver driver` as well as the `TestData testData`.
   You can try this snnipet:  
   ```java
   @Test
   public void verifyLogin() {
       driver.get(testData.getUrl());

       WebElement usernameWebElement = driver.findElement(By.id("username"));
       WebElement passwordWebElement = driver.findElement(By.id("password"));
       WebElement loginBtnWebElement = 
           driver.findElement(By.xpath("//button[@class='button']"));
    
       usernameWebElement.sendKeys(testData.getUsername());
       passwordWebElement.sendKeys(testData.getUsername());
       loginBtnWebElement.click();
   }
   ```


4. Lastly add the new class to the testng.xml file inside the `<suite></suite>` block. Example:
    ```xml
    <test name="Whatever name you like">
      <classes>
        <class name="com.example.testscases.TestGoogle"/>
      </classes>
    </test> <!-- Test -->
    ```
   
That's it. You have successfully added a new test case.

---

## Usage

Now you are all set to run the test cases. To run it using an IDE, just right-click on the testng.xml file and select run.

If you are using a text editor you have to run this command: `java org.testng.TestNG testng.xml`

This will run the test cases with the default configuration values. In the next section we are going to see how to customize this parameters.

---

## Contributing

Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

Please make sure to update tests as appropriate.

