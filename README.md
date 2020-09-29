# [The Joy of Kotlin](https://www.manning.com/books/the-joy-of-kotlin) Exercises

This repository shows the logs of studying Kotlin with [The Joy of Kotlin](https://www.manning.com/books/the-joy-of-kotlin).

[The Joy of Kotlin](https://www.manning.com/books/the-joy-of-kotlin) is to Kotlin as [Effective Java](https://www.oreilly.com/library/view/effective-java/9780134686097/) is to Java. You can learn how to use Kotlin efficiently and safely. Safety is achived due to functional programming paradigm. If you are interested in Kotlin and functional programming, the book perfectly suits you.

## Environment

Use VSCODE instead of IntelliJ IDEA to make an environment incomfortable intentionally. It will be good for learning, and also a directory structure is much simpler.

Install VSCODE extensions:
  - [Kotlin Language](https://marketplace.visualstudio.com/items?itemName=mathiasfrohlich.Kotlin)
  - [Code Runner](https://marketplace.visualstudio.com/items?itemName=formulahendry.code-runner)

Add `-ea` (enable assertion) option for .kt files.
```json
# settings.json
{
  "code-runner.executorMapByFileExtension": {
    ".kt": "cd $dir && kotlinc $fileName -include-runtime -d $fileNameWithoutExt.jar && java -jar -ea $fileNameWithoutExt.jar"
  }
}
```
