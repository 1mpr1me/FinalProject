package ru.myitschool.finalproject;

import java.util.ArrayList;
import java.util.List;

public class Exercise {
    private String title;
    private String description;
    private String difficulty;
    private String category;
    private String code;
    private String solution;
    private List<String> hints;

    public Exercise(String title, String description, String difficulty, String category, String code, String solution, List<String> hints) {
        this.title = title;
        this.description = description;
        this.difficulty = difficulty;
        this.category = category;
        this.code = code;
        this.solution = solution;
        this.hints = hints;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public String getCategory() {
        return category;
    }

    public String getCode() {
        return code;
    }

    public String getSolution() {
        return solution;
    }

    public List<String> getHints() {
        return hints;
    }

    public static List<Exercise> getExercises() {
        List<Exercise> exercises = new ArrayList<>();

        // Basic Python Exercises
        exercises.add(new Exercise(
            "Hello World",
            "Write a program that prints 'Hello, World!' to the console.",
            "Easy",
            "Basic",
            "print('Hello, World!')",
            "print('Hello, World!')",
            List.of("Use the print() function", "Remember to use quotes for strings")
        ));

        exercises.add(new Exercise(
            "Variables and Types",
            "Create variables for your name (string), age (integer), and height (float). Print them in a formatted string.",
            "Easy",
            "Basic",
            "name = 'Your Name'\nage = 20\nheight = 1.75\n\nprint(f'Name: {name}, Age: {age}, Height: {height}m')",
            "name = 'Your Name'\nage = 20\nheight = 1.75\n\nprint(f'Name: {name}, Age: {age}, Height: {height}m')",
            List.of("Use f-strings for formatting", "Remember to use appropriate data types")
        ));

        // Control Flow Exercises
        exercises.add(new Exercise(
            "Even or Odd",
            "Write a program that takes a number as input and prints whether it's even or odd.",
            "Easy",
            "Control Flow",
            "number = int(input('Enter a number: '))\n\nif number % 2 == 0:\n    print('Even')\nelse:\n    print('Odd')",
            "number = int(input('Enter a number: '))\n\nif number % 2 == 0:\n    print('Even')\nelse:\n    print('Odd')",
            List.of("Use the modulo operator (%)", "Remember to convert input to integer")
        ));

        exercises.add(new Exercise(
            "FizzBuzz",
            "Write a program that prints numbers from 1 to 100. For multiples of 3, print 'Fizz'. For multiples of 5, print 'Buzz'. For numbers that are multiples of both, print 'FizzBuzz'.",
            "Medium",
            "Control Flow",
            "for i in range(1, 101):\n    if i % 3 == 0 and i % 5 == 0:\n        print('FizzBuzz')\n    elif i % 3 == 0:\n        print('Fizz')\n    elif i % 5 == 0:\n        print('Buzz')\n    else:\n        print(i)",
            "for i in range(1, 101):\n    if i % 3 == 0 and i % 5 == 0:\n        print('FizzBuzz')\n    elif i % 3 == 0:\n        print('Fizz')\n    elif i % 5 == 0:\n        print('Buzz')\n    else:\n        print(i)",
            List.of("Use nested if statements", "Check for both conditions first")
        ));

        // Functions Exercises
        exercises.add(new Exercise(
            "Factorial Function",
            "Write a function that calculates the factorial of a number. The factorial of a number n is the product of all positive integers less than or equal to n.",
            "Medium",
            "Functions",
            "def factorial(n):\n    if n == 0 or n == 1:\n        return 1\n    return n * factorial(n - 1)\n\n# Test the function\nprint(factorial(5))",
            "def factorial(n):\n    if n == 0 or n == 1:\n        return 1\n    return n * factorial(n - 1)\n\n# Test the function\nprint(factorial(5))",
            List.of("Use recursion", "Handle base cases (0 and 1)")
        ));

        exercises.add(new Exercise(
            "Palindrome Checker",
            "Write a function that checks if a given string is a palindrome (reads the same forwards and backwards).",
            "Medium",
            "Functions",
            "def is_palindrome(text):\n    # Remove spaces and convert to lowercase\n    text = text.replace(' ', '').lower()\n    return text == text[::-1]\n\n# Test the function\nprint(is_palindrome('A man a plan a canal Panama'))",
            "def is_palindrome(text):\n    # Remove spaces and convert to lowercase\n    text = text.replace(' ', '').lower()\n    return text == text[::-1]\n\n# Test the function\nprint(is_palindrome('A man a plan a canal Panama'))",
            List.of("Use string slicing", "Remove spaces and convert to lowercase")
        ));

        // Lists and Dictionaries Exercises
        exercises.add(new Exercise(
            "List Operations",
            "Create a list of numbers and perform the following operations:\n1. Find the sum of all numbers\n2. Find the maximum number\n3. Sort the list in ascending order\n4. Remove duplicates",
            "Medium",
            "Data Structures",
            "numbers = [5, 2, 8, 1, 9, 3, 5, 2]\n\n# Sum\nprint(f'Sum: {sum(numbers)}')\n\n# Maximum\nprint(f'Max: {max(numbers)}')\n\n# Sort\nnumbers.sort()\nprint(f'Sorted: {numbers}')\n\n# Remove duplicates\nunique_numbers = list(set(numbers))\nprint(f'Unique: {unique_numbers}')",
            "numbers = [5, 2, 8, 1, 9, 3, 5, 2]\n\n# Sum\nprint(f'Sum: {sum(numbers)}')\n\n# Maximum\nprint(f'Max: {max(numbers)}')\n\n# Sort\nnumbers.sort()\nprint(f'Sorted: {numbers}')\n\n# Remove duplicates\nunique_numbers = list(set(numbers))\nprint(f'Unique: {unique_numbers}')",
            List.of("Use built-in functions", "Convert set to list for unique values")
        ));

        exercises.add(new Exercise(
            "Word Counter",
            "Write a program that counts the frequency of each word in a given text. Store the results in a dictionary.",
            "Hard",
            "Data Structures",
            "def count_words(text):\n    # Convert to lowercase and split into words\n    words = text.lower().split()\n    \n    # Count frequencies\n    word_count = {}\n    for word in words:\n        word_count[word] = word_count.get(word, 0) + 1\n    \n    return word_count\n\n# Test the function\ntext = 'The quick brown fox jumps over the lazy dog'\nprint(count_words(text))",
            "def count_words(text):\n    # Convert to lowercase and split into words\n    words = text.lower().split()\n    \n    # Count frequencies\n    word_count = {}\n    for word in words:\n        word_count[word] = word_count.get(word, 0) + 1\n    \n    return word_count\n\n# Test the function\ntext = 'The quick brown fox jumps over the lazy dog'\nprint(count_words(text))",
            List.of("Use dictionary.get() method", "Convert text to lowercase first")
        ));

        // File Handling Exercises
        exercises.add(new Exercise(
            "File Reader",
            "Write a program that reads a text file and prints its contents. Handle potential file not found errors.",
            "Medium",
            "File Handling",
            "try:\n    with open('example.txt', 'r') as file:\n        content = file.read()\n        print(content)\nexcept FileNotFoundError:\n    print('File not found!')\nexcept Exception as e:\n    print(f'An error occurred: {e}')",
            "try:\n    with open('example.txt', 'r') as file:\n        content = file.read()\n        print(content)\nexcept FileNotFoundError:\n    print('File not found!')\nexcept Exception as e:\n    print(f'An error occurred: {e}')",
            List.of("Use try-except blocks", "Use with statement for file handling")
        ));

        exercises.add(new Exercise(
            "CSV Parser",
            "Write a program that reads a CSV file and prints the data in a formatted way. Handle different types of data.",
            "Hard",
            "File Handling",
            "import csv\n\ndef read_csv(filename):\n    try:\n        with open(filename, 'r') as file:\n            reader = csv.reader(file)\n            headers = next(reader)\n            print(f'Headers: {headers}')\n            \n            for row in reader:\n                print(f'Row: {row}')\n    except FileNotFoundError:\n        print('File not found!')\n    except Exception as e:\n        print(f'An error occurred: {e}')\n\n# Test the function\nread_csv('data.csv')",
            "import csv\n\ndef read_csv(filename):\n    try:\n        with open(filename, 'r') as file:\n            reader = csv.reader(file)\n            headers = next(reader)\n            print(f'Headers: {headers}')\n            \n            for row in reader:\n                print(f'Row: {row}')\n    except FileNotFoundError:\n        print('File not found!')\n    except Exception as e:\n        print(f'An error occurred: {e}')\n\n# Test the function\nread_csv('data.csv')",
            List.of("Use csv module", "Handle headers separately")
        ));

        // Object-Oriented Programming Exercises
        exercises.add(new Exercise(
            "Bank Account",
            "Create a BankAccount class with methods for deposit, withdraw, and get_balance. Include proper validation.",
            "Medium",
            "OOP",
            "class BankAccount:\n    def __init__(self, initial_balance=0):\n        self.balance = initial_balance\n    \n    def deposit(self, amount):\n        if amount > 0:\n            self.balance += amount\n            return True\n        return False\n    \n    def withdraw(self, amount):\n        if 0 < amount <= self.balance:\n            self.balance -= amount\n            return True\n        return False\n    \n    def get_balance(self):\n        return self.balance\n\n# Test the class\naccount = BankAccount(100)\nprint(account.deposit(50))\nprint(account.withdraw(30))\nprint(account.get_balance())",
            "class BankAccount:\n    def __init__(self, initial_balance=0):\n        self.balance = initial_balance\n    \n    def deposit(self, amount):\n        if amount > 0:\n            self.balance += amount\n            return True\n        return False\n    \n    def withdraw(self, amount):\n        if 0 < amount <= self.balance:\n            self.balance -= amount\n            return True\n        return False\n    \n    def get_balance(self):\n        return self.balance\n\n# Test the class\naccount = BankAccount(100)\nprint(account.deposit(50))\nprint(account.withdraw(30))\nprint(account.get_balance())",
            List.of("Use class methods", "Include input validation")
        ));

        exercises.add(new Exercise(
            "Shape Calculator",
            "Create a base Shape class and derived classes for Circle, Rectangle, and Triangle. Include methods to calculate area and perimeter.",
            "Hard",
            "OOP",
            "import math\n\nclass Shape:\n    def area(self):\n        pass\n    \n    def perimeter(self):\n        pass\n\nclass Circle(Shape):\n    def __init__(self, radius):\n        self.radius = radius\n    \n    def area(self):\n        return math.pi * self.radius ** 2\n    \n    def perimeter(self):\n        return 2 * math.pi * self.radius\n\nclass Rectangle(Shape):\n    def __init__(self, width, height):\n        self.width = width\n        self.height = height\n    \n    def area(self):\n        return self.width * self.height\n    \n    def perimeter(self):\n        return 2 * (self.width + self.height)\n\n# Test the classes\ncircle = Circle(5)\nprint(f'Circle area: {circle.area():.2f}')\nprint(f'Circle perimeter: {circle.perimeter():.2f}')\n\nrectangle = Rectangle(4, 6)\nprint(f'Rectangle area: {rectangle.area()}')\nprint(f'Rectangle perimeter: {rectangle.perimeter()}')",
            "import math\n\nclass Shape:\n    def area(self):\n        pass\n    \n    def perimeter(self):\n        pass\n\nclass Circle(Shape):\n    def __init__(self, radius):\n        self.radius = radius\n    \n    def area(self):\n        return math.pi * self.radius ** 2\n    \n    def perimeter(self):\n        return 2 * math.pi * self.radius\n\nclass Rectangle(Shape):\n    def __init__(self, width, height):\n        self.width = width\n        self.height = height\n    \n    def area(self):\n        return self.width * self.height\n    \n    def perimeter(self):\n        return 2 * (self.width + self.height)\n\n# Test the classes\ncircle = Circle(5)\nprint(f'Circle area: {circle.area():.2f}')\nprint(f'Circle perimeter: {circle.perimeter():.2f}')\n\nrectangle = Rectangle(4, 6)\nprint(f'Rectangle area: {rectangle.area()}')\nprint(f'Rectangle perimeter: {rectangle.perimeter()}')",
            List.of("Use inheritance", "Use abstract methods")
        ));

        return exercises;
    }
}
