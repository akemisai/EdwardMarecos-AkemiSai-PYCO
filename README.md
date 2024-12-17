# EdwardMarecos-AkemiSai-PYCO

## Project Overview
**PYCO** is an innovative social platform that enables users to inventory their clothing items, request outfit suggestions, and participate in a vibrant community that celebrates fashion creativity. Users can create outfit requests centered around specific items or events and receive responses from fashion enthusiasts.

## Table of Contents
1. [Project Overview](#project-overview)
2. [Installation](#Installation)
3. [Tech Stack](#tech-stack)
4. [Key Features](#key-features)
5. [APIs and Libraries Used](#apis-and-libraries-used)
6. [Schema Overview](#schema-overview)
7. [Database Structure](#database-structure)
8. [Folder Structure](#folder-structure)
9. [Future Enhancements](#future-enhancements)
10. [License](#license)

---
   
## Installation
1. Clone the repository:
```
git clone git@github.com:akemisai/EdwardMarecos-AkemiSai-PYCO.git
```
2. Open the project in Android Studio.
3. Build the project
  - Ensure the required Android SDK and Compose dependencies are installed.
  - Sync the project with Gradle.
4. Run the application on emulator or a physical device

---

## Tech Stack
- **Frontend**: Jetpack Compose (Kotlin)
- **Backend**: Firebase Firestore
- **Authentication**: Firebase Authentication
- **APIs**: Remove.bg for background removal, CameraX for in-app camera
- **Design Tools**: Figma for UI/UX design

---

## Key Features
- **Outfit Request System**: Users can create detailed outfit requests and receive suggestions.
- **Personal Closet Management**: Keep track of clothing items with attributes like type, color, and brand.
- **Community Engagement**: Users can respond to others’ outfit requests, upvote, and comment on outfits.

---

## APIs and Libraries Used
- **Firebase Firestore**: Real-time database synchronization.
- **Room Database**: Local storage for offline data management.
- **Remove.bg**: Background removal for user-uploaded images.
- **CameraX**: In-app camera.
- **Coil**: Image loading library for smooth image rendering.
- **Firebase Crashlytics**: Error monitoring and reporting.

## Schema Overview
The schema consists of the following primary entities:
1. **ClothingItem**  
   A representation of an individual clothing item in the system.
2. **Outfit**  
   A collection of clothing items put together to form an outfit.
3. **Request**  
   A request for outfit recommendations or suggestions from other users.
4. **Response**  
   A response to a request, containing the suggested outfit and any comments.
5. **User**  
   A user profile containing information about their outfits, followers, and interactions.

---

## Collections and Documents

### `ClothingItem`
- **id** (String): Unique identifier for the clothing item.
- **name** (String): Name of the clothing item.
- **type** (ClothingType): Enum representing the category of the clothing item (e.g., `TOP`, `BOTTOM`, `SHOE`, `ACCESSORY`).
- **imageUrl** (String): URL of the image representing the clothing item.
- **colour** (Colors): Enum representing the color of the item (e.g., `BLACK`, `BLUE`, `GREEN`).
- **material** (Material): Enum representing the material of the clothing item (e.g., `COTTON`, `POLYESTER`, `LEATHER`).
- **tags** (List<String>): List of tags associated with the clothing item (e.g., `CASUAL`, `FORMAL`).

### `Outfit`
- **id** (String): Unique identifier for the outfit.
- **name** (String): Name of the outfit.
- **top** (DocumentReference): Reference to a `ClothingItem` document for the top.
- **bottom** (DocumentReference): Reference to a `ClothingItem` document for the bottom.
- **shoe** (DocumentReference): Reference to a `ClothingItem` document for the shoes.
- **accessory** (DocumentReference): Reference to a `ClothingItem` document for accessories.
- **createdBy** (String): Name of the user who created the outfit.
- **creatorId** (String): Unique user ID of the creator.
- **public** (Boolean): Whether the outfit is public or private.
- **creatorPhotoUrl** (String): URL of the creator's profile picture.
- **ownerId** (String): User ID of the owner of the outfit.
- **likes** (List<String>): List of user IDs who liked the outfit.
- **tags** (List<String>): List of tags associated with the outfit.
- **timestamp** (Timestamp): Timestamp of when the outfit was created.

### `Request`
- **id** (String): Unique identifier for the request.
- **title** (String): Title of the request.
- **description** (String): Description of the request.
- **ownerId** (String): User ID of the person making the request.
- **ownerName** (String): Name of the person making the request.
- **ownerPhotoUrl** (String): URL of the person's profile picture.
- **responses** (List<String>): List of `Response` IDs related to the request.
- **tags** (List<String>): List of tags associated with the request.
- **timestamp** (Timestamp): Timestamp of when the request was created.

### `Response`
- **id** (String): Unique identifier for the response.
- **title** (String): Title of the response.
- **requestId** (String): ID of the `Request` being responded to.
- **responderId** (String): User ID of the person responding to the request.
- **outfitId** (String): ID of the suggested `Outfit`.
- **outfitName** (String): Name of the suggested outfit.
- **requestDescription** (String): Description of the original request.
- **comment** (String): Comment or additional message related to the response.
- **timestamp** (Timestamp): Timestamp of when the response was created.

### `User`
- **uid** (String): Unique identifier for the user.
- **email** (String): Email address of the user.
- **displayName** (String): Display name of the user.
- **photoURL** (String): URL to the user's profile picture.
- **bookmarkedOutfits** (List<String>): List of `Outfit` IDs bookmarked by the user.
- **followers** (List<String>): List of user IDs who follow this user.
- **following** (List<String>): List of user IDs that the user is following.
- **likesGiven** (List<String>): List of `Outfit` IDs that the user has liked.
- **followersCount** (Int): Total number of followers of the user.
- **followingCount** (Int): Total number of users that the user is following.
- **likesCount** (Int): Total number of likes received by the user.
- **fcmToken** (String?): Firebase Cloud Messaging token for push notifications.

---

## Enum Definitions

### `ClothingType`
- **TOP**
- **BOTTOM**
- **SHOE**
- **ACCESSORY**

### `Colors`
- **BLACK**
- **BLUE**
- **GREEN**
- **RED**
- **WHITE**
- **YELLOW**
- **BROWN**
- **ORANGE**
- **PURPLE**
- **PINK**

### `Material`
- **COTTON**
- **POLYESTER**
- **DENIM**
- **LEATHER**
- **WOOL**
- **SILK**
- **NYLON**
- **RAYON**
- **LINEN**
- **CASHMERE**
- **SHELL**

### `Tags`
- **AUTUMN**
- **OUTDOORS**
- **CUTE**
- **FORMAL**
- **CASUAL**
- **WINTER**
- **SUMMER**

---

## Database Structure

The Firestore database will have the following collections:
- `users`: Stores user profiles.
- `clothingItems`: Stores individual clothing items.
- `outfits`: Stores user-created outfits.
- `requests`: Stores requests made by users for outfit suggestions.
- `responses`: Stores responses to requests, with suggested outfits and comments.

---

## Folder Structure
```
app/
│
├── manifests/
│   └── AndroidManifest.xml
│
├── kotlin+java/
│   └── com.pyco.app/
│       ├── component/
│       ├── models/
│       ├── navigation/
│       └── screens/
│       │   ├── acccount/
│       │   ├── authentication/
│       │   ├── closet
│       │   ├── home/
│       │   ├── outfits/
│       │   ├── requests/
│       │   ├── responses/
│       │   └── upload/
│       ├── ui.theme/
│       └── viewmodel/
│           ├── BaseActivity
│           ├── MainActivity
│           ├── MyApplication
│           └── PycoFirebase
│
├── res/
│   ├── drawable/
│   ├── font/
│   ├── mipmap/
│   ├── values/
│   └── xml/
│
├── res (generated)/
│
└── Gradle Scripts/
    └── build.gradle.kts
```
---

## Future Enhancements
- **Dark Mode Support**: Enhance the `Theme.kt` for dark mode compatibility.
- **Offline Support**: Add Room Database integration for caching data offline.
- **Search Functionality**: Implement a search bar for filtering clothing items.

---

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.



