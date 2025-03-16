# Cryptocurrency Wallet Manager
This project was developed by me as a final part of the 'Modern Java Technologies' course @ Faculty of Mathematics and Informatics, Sofia University. It represents a client-server application designed to simulate a cryptocurrency wallet, which allows users to manage their investments in various cryptocurrencies through a command-line interface. The system enables users to register, log in, deposit and withdraw funds, purchase and sell cryptocurrencies, and get detailed information about their portfolio.

## Key Features
- **Concurrent User Support**: The server is capable of handling multiple clients simultaneously, supporting parallel connections.
- **Authentication System**: Secure user registration and login with encrypted passwords for enhanced security.
- **Wallet Management**: Deposit funds, execute cryptocurrency transactions, and manage your portfolio.
- **Investment Overview**: Track your overall gains or losses and access detailed portfolio reports.
- **Live Market Data**: Integration with CoinAPI to deliver real-time cryptocurrency prices and market insights.

## Architecture
The project is built on a client-server model:

- **Client**: A command-line interface that processes user input and presents the responses in a user-friendly format.
- **Server**: A Java NIO-powered server responsible for: managing user information, handling requests, communicating with a CoinAPI to periodically refresh cryptocurrency price data, and logging errors to a file.
- **Data Persistence**: Information regarding users' authentication and wallet data is saved in binary files to ensure data persistence.
- **Unit Testing**: The project achieves 74% code coverage, with testing implemented using JUnit 5 and Mockito for mocking dependencies.

## Commands
- `help` - Displays a list of available commands and their syntax.
- `register` - Create a new account by providing user credentials.
- `login` - Log in to your account with your credentials.
- `logout` - Exit the current session and log out of your account.
- `deposit <amount>` - Add funds to your wallet in USD.
- `withdraw <amount>` - Withdraw a specified amount from your wallet.
- `list-offerings` - View a list of available cryptocurrencies you can trade and their prices.
- `buy <offering_code> <amount>` - Purchase a specific cryptocurrency using available funds.
- `sell <offering_code>` - Sell a cryptocurrency that you own.
- `get-wallet-summary` - Check the current status of your portfolio, including available funds and investments.
- `get-wallet-overall-summary` - View the total profit or loss across all of your investments.
