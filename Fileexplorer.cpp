#include <iostream>
#include <filesystem>
#include <string>

namespace fs = std::filesystem;

void listFiles(const fs::path& pathToShow, int level = 0) {
    if (fs::exists(pathToShow) && fs::is_directory(pathToShow)) {
        for (const auto& entry : fs::directory_iterator(pathToShow)) {
            auto filenameStr = entry.path().filename().string();
            if (fs::is_directory(entry.status())) {
                std::cout << std::string(level * 2, ' ') << "[DIR] " << filenameStr << std::endl;
                listFiles(entry, level + 1);
            } else if (fs::is_regular_file(entry.status())) {
                std::cout << std::string(level * 2, ' ') << filenameStr << std::endl;
            }
        }
    }
}

int main() {
    std::string path;
    std::cout << "Enter the path to explore: ";
    std::cin >> path;

    fs::path pathToShow(path);
    listFiles(pathToShow);

    return 0;
}
