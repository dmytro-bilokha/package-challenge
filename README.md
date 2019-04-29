# Package Challenge
This repository contains source code for the package challenge solution.

## Solution Description
The solution developed consists of following main logical blocks:
1. Packaging Task Parser.
1. Packaging Task Solver.
1. Packer.

### Packaging Task Parser
The goal of the parser is to try to parse one line which represents a packaging task and either return object
representing the packaging task (`PackagingTask`) or throw an exception containing error
message (`InvalidTaskStringException`). The parser implementation is based on following main assumptions:
* Package's weight limit, thing's weight and thing's cost could have a fractional part. Fractional part for thing's
  weight I've seen in the input sample provided. In the provided sample package's weight limit and thing's cost were
  without fractional part, but I've decided to assume that fractional part is possible.
* Thing's index is integer, because I don't see any value in having fractional part for the index.
* Strings with unpaired braces, more or less than one colon, empty or non-valid thing's definition are not allowed.
* The input string's encoding is UTF-8 and the string could contain Unicode symbols.
* Euro currency symbol before thing's cost is not mandatory.
* Additional constraints on package's weight, thing's weight and cost, maximum amount of things should be checked.

To avoid bugs related to [loss of accuracy](https://dzone.com/articles/never-use-float-and-double-for-monetary-calculatio)
I use `BigDecimal` type for data which could contain fractional part.

### Packaging Task Solver
The solver expects `PackagingTask` object on his input, resolve the packaging task and provide the best layout
represented by `PackagingLayout` object.

From applied mathematics point of view the Package Challenge is a 
[0/1 Knapsack Problem](https://en.wikipedia.org/wiki/Knapsack_problem#0/1_knapsack_problem).
Because there were no any specific requirements (algorithm to use, performance to achieve) and maximum amount of 
things to package is relatively small (15), the simple brute force method has been chosen. It means, for each packaging
task represented by one line in the input file solution search will run in time O(2ⁿ), where n is the number of things.
There are methods with higher performance, but I've decided to sacrifice performance in sake of implementation
simplicity. The solver is single-threaded also because of simplicity reasons.

### Packer
The `Packer` reads a file from the provided path, uses the parser to get packaging tasks list, then passes tasks
one by one to the solver and collects results to return them as a single string. Basing on the output sample provided,
I assumed that each solution (list of indexes) should be terminated by the new line character sequence.
 
In case the packer could not read the file, it throws the `APIException` with the message containing file path.
If the parser reports a parsing error for any line, the packer will continue to call the parser and collect error
messages. After end of parsing the file, the packer will throw the `APIException` with all the errors and line numbers.
The solver won't be called in such scenario. I've implemented the packer in such way because of following reasons:
* There were no instructions to process a file with non-valid lines. So, I assumed it should not be processed.
* From my practice, in case of non-valid records in the file, users prefer to get list of all the errors, not only the
  first one.

## Infrastructure Description
In this part I'd like to shed some light on infrastructure-related design:
1. Tests.
1. Static code analysis.

### Tests
The solution contains unit and integration tests. In this project I use the following definitions:
* I call a test 'unit test' if it tests logic only from one of the project's classes. The example is
  the `PackageLayoutTest`, it tests only the `PackageLayout` class.
* I consider a test as 'integration test' if it tests logic from more than one of the project's classes. The example is
  the `PackagingTaskSolverTest`, because it tests the `PackagingTaskSolver`, but the solver internally uses logic from
  the `PackageLayout` class.
Unit tests are executed on the *Maven test* phase, integration test run on the *Maven verify* phase.
I've used TestNG framework for tests instead of jUnit, because I find TestNG more flexible and easier to use with
integration tests.

### Static Code Analysis
In this project I use [Checkstyle](http://checkstyle.sourceforge.net/), [PMD](https://pmd.github.io/) and
[SpotBugs](https://spotbugs.github.io/) to enforce some code style, best practices and prevent bugs. Usually these
checks are integrated in the Jenkins pipeline, but here I've used Maven plugins.
