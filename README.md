<div align="center">
  <img src="https://github.com/user-attachments/assets/3631aea8-bd86-4831-b1ac-f69f7c492674" alt="logo" width="200px">
  
  # MoneyMate

  ### Simple spending tracker

</div>

> [!NOTE]
> This is a pet project and it may contain flaws. It may not be ready for full use, but it can be a great foundation for your application.


## Screenshots

<div align="center">
  <div>
    <img src="https://github.com/user-attachments/assets/665d52d1-157f-489b-9aa5-b1a1a051cee3" width="24%" />
    <img src="https://github.com/user-attachments/assets/86198498-e69e-41b1-8337-337cc7dbce05" width="24%" />
    <img src="https://github.com/user-attachments/assets/a827c418-3e48-4d18-ac44-66d1f7a1d974" width="24%" />
    <img src="https://github.com/user-attachments/assets/6d6ff82c-9ea6-41d4-b3e6-883689a5efb1" width="24%" />
  </div>
</div>

## Features
- **Track Your Spendings** – Log your expenses with categories, amounts, and dditional details like a short description or shopping list.
- **Custom Categories** – Organize your spendings by creating and managing categories that suit your needs.
- **Detailed Statistics** – Gain insights into your financial habits with spending reports for any selected period.
- **Budget Planning** – Set daily, weekly, or monthly budget to stay in control of your expenses.
- **Cloud Sync** – Sync your data across multiple devices ([money-mate-server](https://github.com/dn0ne/money-mate-server)).

## Build
1. **Get the Source Code**  
   - Clone the repository or download the source code:
     ```bash
     git clone https://github.com/dn0ne/money-mate.git
     ```

2. **Open project in Android Studio**  
   - Launch Android Studio.  
   - Select **File > Open** and navigate to the project's folder.  
   - Click **OK** to open the project.

3. **Run the project**  
   - Wait for the project to sync and build (Gradle sync may take some time).  
   - Ensure a device or emulator is connected.  
   - Click the **Run** button or press `Shift + F10` to build and launch the app.  

That's it! The app should now be running.

## Cloud

To use your variant of [money-mate-server](https://github.com/dn0ne/money-mate-server), replace the url with your own in

`money-mate/shared/src/commonMain/kotlin/com.dn0ne.moneymate/app/di/AppModule.kt`
