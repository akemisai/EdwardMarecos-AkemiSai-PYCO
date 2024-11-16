# EdwardMarecos-AkemiSai-PYCO

## Project Overview
**PYCO** is an innovative social platform that enables users to inventory their clothing items, request outfit suggestions, and participate in a vibrant community that celebrates fashion creativity. Users can create outfit requests centered around specific items or events and receive responses from fashion enthusiasts.

## Table of Contents
1. [Project Overview](#project-overview)
2. [Tech Stack](#tech-stack)
3. [Key Features](#key-features)
4. [APIs and Libraries Used](#apis-and-libraries-used)
5. [Database Schema](#database-schema)

## Tech Stack
- **Frontend**: Jetpack Compose (Kotlin)
- **Backend**: Firebase Firestore, Room Database for offline storage
- **Authentication**: Firebase Authentication
- **APIs**: ML Kit for background removal, Google API for image processing
- **Design Tools**: Figma for UI/UX design

## Key Features
- **Outfit Request System**: Users can create detailed outfit requests and receive suggestions.
- **Personal Closet Management**: Keep track of clothing items with attributes like type, color, and brand.
- **Community Engagement**: Users can respond to others’ outfit requests, upvote, and comment on outfits.
- **Responsive Design**: Optimized for both portrait and landscape modes for mobile and tablet.

## APIs and Libraries Used
- **Firebase Firestore**: Real-time database synchronization.
- **Room Database**: Local storage for offline data management.
- **ML Kit**: Background removal for user-uploaded images.
- **Retrofit**: For handling HTTP requests and API calls.
- **Coil**: Image loading library for smooth image rendering.
- **Firebase Crashlytics**: Error monitoring and reporting.

## Database Schema
Our current plan for the database structure:
def tbd and will be better organized soon :3 

### User Table
- `user_id` (Primary Key)
- `username` (Unique)
- `email` (Unique)
- `profile_picture_url`

### Outfit Requests Table
- `request_id` (Primary Key)
- `user_id` (Foreign Key)
- `title`
- `tags`
- `description`
- `created_at`

### Outfits Table
- `outfit_id` (Primary Key)
- `request_id` (Foreign Key)
- `user_id` (Foreign Key)
- `image_url`
- `details`
- `likes`
- `comments_count`

### Closet Items Table
- `item_id` (Primary Key)
- `user_id` (Foreign Key)
- `item_type`
- `color`
- `brand`
- `tags`
