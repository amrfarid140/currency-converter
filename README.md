# Currency Converter

This application helps users to convert from one currency to the other. It uses Euro as the base currency. The application refreshes its data every five seconds without disrupting the UI.

Asynchronous execution of business logic is done used RxJava.
API calls are made using Retrofit.
Dependency Injection is done using Dagger Android
The application is built with Clean architecture in mind and the UI state management is done using Model-View-ViewModel.

There are 4 modules in the project. Each module represents one of the Clean architecture layer

- Data module: contains repositories implementation to fetch data from the API
- Domain module: defines common API interfaces (contracts) and includes business logic which is wrapped in use cases.
- Presentation module: contains the view model and state creation/modification logic
- UI module: handles capturing user input and representing the state in a visual format

Each module is unit tested and mocks are provided via mockito.

Mockk is used to mock view models in Espresso tests. This is due the limitation of using mocktio-inline on the Android runtime.
