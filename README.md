# Unix Shell Implementation with Java

This is my implementation of the UNIX Shell (command-line interpreter - CLI) using Java. In this project, I got to strengthen my Java coding skills with **Object-Oriented Programming** (interfaces, overriding, etc.), **Java System Calls**, and **Multithreading/Concurrent Programming** (Threads, Synchronization Problems, LinkedBlockingQueue, Poison Pill, etc.)

Supported commands include: 
- Working Directory (File System Navigation) Commands: **ls**, **cd**, **pwd**
- Text Operations: **head**, **grep**, **wc**, **uniq**
- Redirection (**>**) and Piping (**|**)
- Background Processes: **&** (indicate background process), **kill [job number]** (kill a specified process), **repl_jobs** (list running processes)
  
Error messages are also included.
  
* For Piping (chaining) commands, each one will run concurrently by default (instead of sequentially) to save execution time, especially for large files. 

The entry point of Danny's Unix Shell is in **main/ConcurrentREPL.java** which will runs a _REPL (Read-Eval-Print Loop)_ which takes a shell command and prints out the correct response (unless redirected to a file, in which case no output is printed to the console).





