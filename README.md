# Kotlin Public Declarations Extractor

This Kotlin application extracts and prints all public declarations (functions, classes, objects, and properties) from a given Kotlin project or library source directory.

### Clone the Repository
```sh
git clone https://github.com/your-repo/kotlin-public-declarations
cd kotlin-public-declarations
```

## Building & Running

### Using Gradle
1. **Build the project**
   ```sh
   ./gradlew build
   ```
2. **Run the program with arguments**
   ```sh
   ./gradlew run --args="/path/to/kotlin/source"
   ```

## Example Usage
```sh
./gradlew run --args="./Exposed"
```
Output:
```
fun declaration1()
class A {
   fun declaration2()
}
```
