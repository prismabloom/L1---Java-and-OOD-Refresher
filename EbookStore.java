import java.util.ArrayList;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.io.File;

public class EbookStore {
    public static void main(String[] args) {
        System.out.println("Welcome to the BookStore!");
        String[] titles = {"The Place Beyond the Winds", "The Heart of London", "Japanese Fairy Tales", "The Gentle Grafter", "Clover"};
        String[] location = {"Beyond-The-Wind.txt", "The-Heart-of_London.txt", "Japanese-Fairy-Tales.txt", "The-Gentle-Grafter.txt", "Clover.txt"};
        Scanner userInput = new Scanner(System.in);
        ArrayList<Book> unownedBooks = new ArrayList<Book>();
        ArrayList<Book> ownedBooks = new ArrayList<Book>();
        boolean bookNotFound = false; //Used to see if the book is found or not, and to limit the number of diffrent search loops that must be used
        for(int i=0; i<titles.length; i++) {
            unownedBooks.add(new Book(titles[i], location[i])); //Begins by making a new book object from the titles and locations, all unbought
        }
        while(true){
            EbookStore.printStore(unownedBooks, ownedBooks);
            String input;
            System.out.print("Enter the title of the book you want to purchase or read (or press s and enter to enter search mode, or press e and enter to exit): ");
            input = userInput.nextLine().toLowerCase();
            bookNotFound = true;
            if(input.equals("s")) { //Activates search mode
                System.out.println();
                System.out.println();
                System.out.println("*** Search Mode Activated ***");
                System.out.println();
                while(true) {
                    EbookStore.printStore(unownedBooks, ownedBooks);
                    System.out.print("Enter the title of the book you want to buy or search (or press e and enter to exit search mode): ");
                    input = userInput.nextLine().toLowerCase();
                    bookNotFound = true;
                    if(input.equals("e")) {
                        break;
                    }
                    if(bookNotFound) {
                        for(int i=0; i<ownedBooks.size(); i++) {
                            if(ownedBooks.get(i).title.toLowerCase().equals(input)) {
                                ownedBooks.get(i).searchOpen(userInput);
                                bookNotFound = false;
                            }
                        }
                    }
                    if(bookNotFound) {
                        for(int j=0; j<unownedBooks.size(); j++) {
                            if(unownedBooks.get(j).title.toLowerCase().equals(input)) {
                                ownedBooks.add(unownedBooks.get(j));
                                unownedBooks.remove(j);
                                bookNotFound = false;
                            }
                        }
                    }
                    if(bookNotFound) {
                        System.out.println("Sorry, that book was not found");
                    }
                    System.out.println();
                    System.out.println();
                }
                continue;
            }
            if(input.equals("e")) {
                break;
            }

            if(bookNotFound) {
                for(int i=0; i<ownedBooks.size(); i++) {
                    if(ownedBooks.get(i).title.toLowerCase().equals(input)) {
                        while(ownedBooks.get(i).open(userInput)) { //This loop is present becuase when owned books returns true, it must run again, becuase that means the user inputted to go backwards, requiring teh reinitalization of the scanner
                        }
                        bookNotFound = false;
                    }
                }
            }
            if(bookNotFound) {
                for(int j=0; j<unownedBooks.size(); j++) {
                    if(unownedBooks.get(j).title.toLowerCase().equals(input)) {
                        ownedBooks.add(unownedBooks.get(j));
                        unownedBooks.remove(j);
                        bookNotFound = false;
                    }
                }
            }
            if(bookNotFound) {
                System.out.println();
                System.out.println("Sorry, that book was not found");
            }
            System.out.println();
            System.out.println();
        }
        userInput.close();
    }

    public static void printStore(ArrayList<Book> booksYouDontOwn, ArrayList<Book> booksYouOwn){
        System.out.println();
        System.out.println("Owned Books: ");
        for(int i=0; i<booksYouOwn.size(); i++) {
            System.out.println(booksYouOwn.get(i).title);
        }
        if(booksYouOwn.size()<1) {
            System.out.println("None");
        }
        System.out.println();
        System.out.println();
        System.out.println("Available for Purchase: ");
        for(int j=0; j<booksYouDontOwn.size(); j++) {
            System.out.println(booksYouDontOwn.get(j).title);
        }
        if(booksYouDontOwn.size()<1) {
            System.out.println("None");
        }
        System.out.println();
    }
}

class Book {
    String title;
    String path;
    int userPlace;
    public Book(String name, String location) {
        title = name;
        path = location;
        userPlace = 0;
    }
    public void searchOpen(Scanner userInput) {
        System.out.println();
        System.out.println();
        try {
            File thisBook = new File(path);
            Scanner myReader = new Scanner(thisBook);
            File stopWordsFile = new File("Stop-Words.txt");
            Scanner stopWordsReader = new Scanner(stopWordsFile);
            ArrayList<String> words = new ArrayList<String>();
            ArrayList<Integer> wordCount = new ArrayList<Integer>();
            ArrayList<String> stopWords = new ArrayList<String>();
            String nextWord;
            boolean isNew;
            while(true) {
                System.out.println("Press enter to generate the ten most common non stop words in " + title);
                System.out.println("Enter some charaters or words and then press enter to display all lines in " + title + "with the given string.");
                System.out.print("Press e then enter to exit this book: ");
                String input = userInput.nextLine().toLowerCase();
                if(input.equals("e")) {
                    myReader.close();
                    stopWordsReader.close();
                    return;
                }
                if(input.equals("")) {
                    System.out.println("Calculating the Ten most common words in the book (this will take a while)...");
                    while (stopWordsReader.hasNext()) { //Creates the list of stop words
                        nextWord = stopWordsReader.next().toLowerCase();
                        stopWords.add(nextWord);
                    }
                    stopWordsReader.close();
                    while (myReader.hasNext()) {
                        nextWord = myReader.next().toLowerCase();
                        isNew = true;
                        
                        for(int i=0; i<stopWords.size(); i++) { //The word is not included if its a stop word
                            if(nextWord.equals(stopWords.get(i))) {
                                isNew = false;
                                break;
                            }
                        }
                        if(isNew) {
                            for(int i=0; i<words.size(); i++) {
                                if(words.get(i).equals(nextWord)) {
                                    wordCount.set(i, wordCount.get(i) + 1);
                                    isNew = false;
                                    break;
                                }
                            }
                        }
                        if(isNew) { //If the word is not found anywhere else, it is added to the list
                            words.add(nextWord);
                            wordCount.add(1);
                        }
                    }
                    System.out.println("Determining the Most Common of the Words (besides stop words)...");
                    int size = wordCount.size();
                    for (int i = 0; i < size - 1; i++) { //An implementaion of bubble sort to rearange wordCount and words based on the frequency of each word
                        for (int j = 0; j < size - i - 1; j++) {
                            if (wordCount.get(j) > wordCount.get(j + 1)) {
                                int temp = wordCount.get(j);
                                String tempWord = words.get(j);
                                wordCount.set(j, wordCount.get(j + 1));
                                words.set(j, words.get(j + 1));
                                wordCount.set(j+1, temp);
                                words.set(j+1, tempWord);
                            }
                        }
                    }
                    if(size>9) {
                    for(int i=size-1; i>size-11; i--) { //Prints the 
                        System.out.println("\"" + words.get(i) + "\"" + " Appears " + wordCount.get(i)  + " times"); //Prints the 10 most common words
                    }
                    }
                    System.out.println();
                    System.out.println();
                }
                else {
                    System.out.println();
                    System.out.println("Printing all lines with " + input + ": ");
                    System.out.println();
                    Scanner mySearcher = new Scanner(thisBook);
                    while (mySearcher.hasNextLine()) {
                        String bookLine = mySearcher.nextLine();
                        if(bookLine.toLowerCase().contains(input)) {
                            System.out.println(bookLine); //If the line contains the word, it is printed immediately
                        }
                    }
                    mySearcher.close();
                    System.out.println();
                    System.out.println();
                }
            }

        } catch (FileNotFoundException ee) {
            System.out.println("The file was not found");
            ee.printStackTrace();
        }
    }

    public boolean open(Scanner userInput) {
        try {
            File thisBook = new File(path);
            Scanner myReader = new Scanner(thisBook);
            int lineCounter = 0;
            while(lineCounter < userPlace) {
                myReader.nextLine();
                lineCounter++;
            }
            lineCounter = 0;
            while(true) {
                while (myReader.hasNextLine() && lineCounter < 20) {
                    String bookLine = myReader.nextLine();
                    System.out.println(bookLine);
                    lineCounter++;
                }
                userPlace += lineCounter;
                lineCounter = 0;
                System.out.print("Press enter to continue, press e and enter to exit, or press b and enter to go back: ");
                String pageInput = userInput.nextLine();
                if(pageInput.toLowerCase().equals("e")) {
                    myReader.close();
                    userPlace-=20;
                    return false;
                }
                if(pageInput.toLowerCase().equals("b")) {
                    userPlace-=40;
                    myReader.close(); //If the user inputs back, we change user place by 40 and perform open again, reinitalizing teh scanner to the last page
                    return true; //Causes the loop in main to continue until back is no longer the input
                }
            }
            } catch (FileNotFoundException ee) {
            System.out.println("The file was not found");
            ee.printStackTrace();
            return false;
        }
    }
}