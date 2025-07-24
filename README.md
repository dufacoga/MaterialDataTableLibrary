# 📊 Material DataTable Library for Jetpack Compose (Material 3)

<p align="center">
  <a href="https://github.com/dufacoga/MaterialDataTableLibrary/issues"><img src="https://img.shields.io/github/issues/dufacoga/MaterialDataTableLibrary"/></a>
  <a href="https://github.com/dufacoga/MaterialDataTableLibrary/stargazers"><img src="https://img.shields.io/github/stars/dufacoga/MaterialDataTableLibrary"/></a>
  <a href="https://github.com/dufacoga/MaterialDataTableLibrary/network/members"><img src="https://img.shields.io/github/forks/dufacoga/MaterialDataTableLibrary"/></a>
  <a href="https://github.com/dufacoga/MaterialDataTableLibrary/commits/main"><img src="https://img.shields.io/github/last-commit/dufacoga/MaterialDataTableLibrary"/></a>
  <a href="https://github.com/dufacoga/MaterialDataTableLibrary/blob/main/CONTRIBUTING.md"><img src="https://img.shields.io/badge/contributions-welcome-brightgreen.svg?style=flat"/></a>
  <a href="https://github.com/dufacoga/MaterialDataTableLibrary/blob/main/LICENSE"><img src="https://img.shields.io/github/license/dufacoga/MaterialDataTableLibrary"/></a>
  <br />
  <a href="https://www.paypal.com/donate/?business=R2J9NH55HXKGJ&no_recurring=0&currency_code=USD"><img src="https://img.shields.io/badge/PayPal-Donate-blue.svg"/></a>
  <a href="https://www.patreon.com/dufacoga"><img src="https://img.shields.io/badge/Patreon-Become%20a%20Patron-black.svg"/></a>
  <a href="https://ko-fi.com/dufacoga"><img src="https://img.shields.io/badge/Ko--fi-Buy%20me%20a%20coffee-FFFFFF.svg?logo=ko-fi&logoColor=white"/></a>
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
	headers = headers,
	dataLoader = dataLoaderFromListWithDelay(sourceProvider = { dataList }, rowMapper = { it }),
	onEdit = { rowIndex, rowData -> val id = rowData[0]; println("Edit row at index $rowIndex") },
	onDelete = { rowIndex, rowData -> val id = rowData[0]; println("Delete row at index $rowIndex") },
	onMoreVert = { rowIndex, rowData -> val id = rowData[0]; println("MoreVert row at index $rowIndex") },
	columnSizeAdaptive = true,
	columnWidth = 150.dp,
	editOption = true,
	deleteOption = true,
	horizontalDividers = true,
	verticalDividers = true,
	childState = childState,
	width = width,
	height = height,
	totalItems = dataList.size
)
```

You can customize everything from action icons to layout paddings if needed.

---

## 📦 How to Include in Your Project

You can easily include this library in your Android project using **JitPack** — no need to download manually.

### Step 1: Add JitPack to your `settings.gradle.kts` (Project level)

```kotlin
dependencyResolutionManagement {
		repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
		repositories {
			mavenCentral()
			maven { url = uri("https://jitpack.io") }
		}
	}
```

<details>
<summary>If you're using Groovy instead of Kotlin DSL</summary>

```groovy
allprojects {
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```

</details>

### Step 2: Add the library dependency in your `build.gradle.kts` (Module level)

```kotlin
dependencies {
	        implementation("com.github.dufacoga:MaterialDataTableLibrary:v1.0.0")
	}
```

🔖 Replace `1.0.0` with the latest tag from [https://jitpack.io/#dufacoga/MaterialDataTableLibrary](https://jitpack.io/#dufacoga/MaterialDataTableLibrary)

---

### 📁 Optional: Clone Manually

If you prefer, you can still clone the repository directly:

```bash
git clone https://github.com/dufacoga/MaterialDataTableLibrary.git
```

Then import the `MaterialDataTable` module in Android Studio via:  
**`File > New > Import Module...`**

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
