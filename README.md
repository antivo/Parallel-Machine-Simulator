Parallel Machine Simulator
==========================
(2015)
@Author: stjepan.antivo.ivica@gmail.com

Pre - Interpreter statements
* comments start with '#'
* :load <from_file>	
* :pram <MODEL>         ; CRCW, CREW, EREW, ERCW, RAM
* :reset
* :verbose <Boolean>

Interpreter statements 
* Syntax is Python - like
* If the memory model is RAM all the statements that are in Python 2.7 interpreter are available
* Otherwise Assignment(=), For, If, Parallel, Pass, Print and While statements are available and no function can be called within parallel block
* reduce(function, iterable[, initializer])
* scan(function, iterable[, state])
* Assigments in non RAM memory model can be prefixed with ~ (for example ~a = 4). Location on the left will not be checked against constrains of selected PRAM memory model.
