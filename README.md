# 📊 Material DataTable Library for Jetpack Compose (Material 3)

<p align="center">
  <a href="https://github.com/dufacoga/MaterialDataTableLibrary/issues"><img src="https://img.shields.io/github/issues/dufacoga/MaterialDataTableLibrary"/></a>
  <a href="https://github.com/dufacoga/MaterialDataTableLibrary/stargazers"><img src="https://img.shields.io/github/stars/dufacoga/MaterialDataTableLibrary"/></a>
  <a href="https://github.com/dufacoga/MaterialDataTableLibrary/network/members"><img src="https://img.shields.io/github/forks/dufacoga/MaterialDataTableLibrary"/></a>
  <a href="https://github.com/dufacoga/MaterialDataTableLibrary/commits/main"><img src="https://img.shields.io/github/last-commit/dufacoga/MaterialDataTableLibrary"/></a>
  <a href="https://github.com/dufacoga/MaterialDataTableLibrary/blob/main/CONTRIBUTING.md"><img src="https://img.shields.io/badge/contributions-welcome-brightgreen.svg?style=flat"/></a>
  <a href="https://github.com/dufacoga/MaterialDataTableLibrary/blob/main/LICENSE"><img src="https://img.shields.io/github/license/dufacoga/MaterialDataTableLibrary"/></a>
</p>

A responsive, paginated, and reusable **DataTable component** built with **Jetpack Compose** and following **Material Design 3** guidelines.  
Perfect for Android developers who want a clean, customizable table with support for actions, scroll, and loading indicators.

---

## 🎮 Demo Preview

<p align="center">
  <img src="https://github.com/user-attachments/assets/68c17738-b9f3-426d-96db-e8c724545037" alt="DataTable Demo" width="90%" />
</p>

---

## ✨ Features

- 🔁 Full pagination (first, previous, next, last)
- 💡 Column width control and responsive sizing
- 🖱️ Smooth horizontal + vertical scrolling
- ✅ Edit and Delete buttons per row
- 🌀 Loading indicator on data fetch
- ✏️ Text truncation with ellipsis for single-line display
- 📐 Customizable width and height
- 🌐 Material Design 3 compliant

---

## 🧪 Test Screen

A demo composable `MaterialDataTableTestScreen()` is included in the library module to preview and validate the table component quickly.

---

## 🔧 How to Use

You can integrate the component in your app like this:

```kotlin
MaterialDataTableC(
    headers = listOf("ID", "Name", "Role", "Email"),
    dataLoader = { page, pageSize -> 
        // Return your data here as List<List<String>>
        myApi.fetchUsers(page, pageSize)
    },
    onEdit = { rowIndex -> /* your edit logic */ },
    onDelete = { rowIndex -> /* your delete logic */ },
    width = 400.dp,
    height = 600.dp
)
```

You can customize everything from action icons to layout paddings if needed.

---

## 📦 How to Include in Your Project

Clone or include this module (`MaterialDataTable`) as a library in your existing **Android project**.

```bash
git clone https://github.com/dufacoga/MaterialDataTableLibrary.git
```

Or import the module in **Android Studio** via `File > New > Import Module...`

---

## 🗂️ Project Structure

```
📁 MaterialDataTableLibrary/
├── MaterialDataTable/         # Library module
│   └── MaterialDataTable.kt   # Core reusable component
│   └── TestScreen.kt          # Composable for local preview/testing
│   └── build.gradle.kts
│
├── app/                       # Optional preview usage app
│   └── MainActivity.kt
│
├── README.md
└── CONTRIBUTING.md
```

---

## 👨‍💻 Built With

- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Material 3 Compose](https://developer.android.com/jetpack/compose/material3)
- [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)

---

## 📄 License

This project is licensed under the [MIT License](LICENSE).

---

## 👤 Author

**Douglas Cortes**  
💼 [LinkedIn](https://www.linkedin.com/in/dufacoga)  
🌐 [dufacoga.github.io](https://dufacoga.github.io)
