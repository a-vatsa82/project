#include <iostream>
#include <filesystem>
#include <string>
#include <chrono>
#include <iomanip>
#include <fstream>

namespace fs = std::filesystem;

// Global log file
std::ofstream logFile("file_explorer.log", std::ios::app);

// Logging function
void log(const std::string& level, const std::string& message) {
    auto now = std::chrono::system_clock::now();
    auto in_time_t = std::chrono::system_clock::to_time_t(now);
    logFile << std::put_time(std::localtime(&in_time_t), "%Y-%m-%d %X") 
            << " [" << level << "] " << message << std::endl;
}

void listFiles(const fs::path& pathToShow, int level = 0) {
    if (fs::exists(pathToShow) && fs::is_directory(pathToShow)) {
        log("INFO", "Listing directory: " + pathToShow.string());
        for (const auto& entry : fs::directory_iterator(pathToShow)) {
            auto filenameStr = entry.path().filename().string();
            if (fs::is_directory(entry.status())) {
                std::cout << std::string(level * 2, ' ') << "[DIR] " << filenameStr << std::endl;
                log("INFO", "Found directory: " + filenameStr);
                listFiles(entry, level + 1);
            } else if (fs::is_regular_file(entry.status())) {
                std::cout << std::string(level * 2, ' ') << filenameStr << std::endl;
                log("INFO", "Found file: " + filenameStr);
            }
        }
    } else {
        log("ERROR", "Path does not exist or is not a directory: " + pathToShow.string());
    }
}

int main() {
    log("INFO", "File Explorer started");

    std::string path;
    std::cout << "Enter the path to explore: ";
    std::cin >> path;

    log("INFO", "User entered path: " + path);

    fs::path pathToShow(path);
    listFiles(pathToShow);

    log("INFO", "File Explorer finished");
    return 0;
}
