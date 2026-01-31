### Unit Testing Preferences

- **Test Method Naming**: Use the pattern `itShouldDoXWhenY` (e.g., `itShouldReturnParsedMapWhenValidQueryStringIsProvided`).
- **Test Object Creation**: Use [Instancio](https://www.instancio.org/) to create test objects. Do not use it for simple types like `String`, `Long`, or `Integer` when a literal or simple value suffices.
- **Test Object Initialization**: Do not create empty test objects. Assign values only to the fields that are actually used or relevant to the source methods being tested.
- **Mockito Mocking**: Do not use `any()` or `anyX()` matchers (e.g., `Mockito.anyString()`). Use specific, meaningful values that reflect the test case.
- **Variable Naming**: Give result objects meaningful names that describe what they represent (e.g., `parsedParams`), rather than generic names like `result`.
- **Test Structure**: Do not use `// given`, `// when`, `// then` comments. Use empty lines (spaces) to separate the different phases of the test.
- **Code Refactoring**: Do not refactor the source code unless specifically requested. Focus strictly on writing the tests.
- **SUT Instantiation**: Instantiate systems under test (SUT) using setup methods (e.g., `@BeforeEach` in JUnit 5); do not use direct instantiation in field declarations.
- **Assertion Inlining**: For simple methods (e.g., getters returning constants), inline the method call directly within the assertion (e.g., `assertEquals(EXPECTED_VALUE, sut.getValue())`).
- **No Integration Tests in Unit Tests**: Do not force integration tests inside unit test classes. If a method requires testing the system in integration (e.g., starting a server, connecting to a database), omit it from the unit test suite.
- **Testing Private Members**: If you need to test private members or classes and cannot change the visibility, use the Reflection API to temporarily change the accessibility (e.g., `setAccessible(true)`). Wrap the reflection logic in a private helper method within the test class to keep test methods clean and focused.
