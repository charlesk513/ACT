# Revision_manager
A command-line application designed to help students organize and manage revision materials efficiently. The system allows users to store, update, and access PDFs, books, and other study resources by course unit.

🚀 Features

      📂 Add revision materials (PDFs, books, notes)
      🗂️ Organize resources by course unit
      🔄 Update existing materials
      🔍 View available resources per subject
      ⚡ Lightweight CLI interface
      💾 Local file storage support
      🛠️ Tech Stack
	
Language: Java

Concepts Used:

Object-Oriented Programming (OOP)
File Handling (File, BufferedReader, FileWriter)
Collections (ArrayList, HashMap)
Interface: Command Line Interface (CLI)

📂 Project Structure

revision-manager/

│── src/

│    ├── Main.java

│    ├── Material.java

│    ├── CourseUnit.java

│    ├── RevisionManager.java
│
│── data/

│    ├── course_units/

│    ├── materials/

│

│── README.md



Example CLI commands:

add "Data Structures" "trees.pdf"
view "Operating Systems"
update "Computer Networks" "network_notes.pdf"
💡 System Design Overview
Each Course Unit is represented as an object.
Each Material (PDF/book) is linked to a course unit.
Data is stored locally using file handling.

Course Unit → List of Materials

🎯 Future Improvements

    🔐 Add user authentication
    🗄️ Integrate a database (e.g., PostgreSQL or MySQL)
    🔎 Implement advanced search (by topic, keyword)
    🌐 Cloud storage integration
    🖥️ GUI version using JavaFX or Swing

👨‍💻 Author

Kabunga Charles

Colaboration: Lugonvu Joel, Ssenyonjo Twaha

📄 License

This project is open-source and available under the MIT License.
