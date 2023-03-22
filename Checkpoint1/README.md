# Checkpoint 1
Name: Farid Hamid, Lourenco Velez
Student ID: 1067867, 1102204

# Acknowledgements
This project was build off of Professor Fei Song's C1-Package

# How to build
1. <make>
2. <java -cp /usr/share/java/cup.jar:. CM test/filename.cm> or <java -cp /usr/share/java/cup.jar:. CM test/filename.cm -a>
3. <make clean> 

# Test Instructions
- 1.cm: <java -cp /usr/share/java/cup.jar:. CM test/1.cm> or <java -cp /usr/share/java/cup.jar:. CM test/1.cm -a>

- 2.cm: This should throw a lexical error on line 5 because `in t x;` is not a valid type specifier. It'll also produce a syntactic error on line 14, since a variable cannot be declared after a statement list

- 3.cm: This should throw a syntactic error on line 4 because you can't declare and initialize a variable on the same line. Also line 5 produces a lexical error for '!'

- 4.cm: This should throw an syntatic error because the while loop on line 9 contains empty parameters instead of the keyword void. 

- 5.cm: This should throw a syntactic error in line 9 as the while loop contains an invalid expression as a parameter.

