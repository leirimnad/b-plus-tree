# B+ tree

**This is the Java implementation of B+ tree structure.**

It supports inserting, getting and deleting programmatically.

### File parser

The file parser is used for testing and demonstration purposes. 
It creates a tree (or uses one), 
the **keys** of which are of the type `RationalNumber`, 
and the **values** of which are of the type `String`.
It can parse the commands from the text file.

To successfully parse a file, the file should contain the following in its first line:
```text 
BRANCHING FACTOR XXX
```
, where `XXX` is the branching factor (maximum amount of children a node can have) of the tree.

Example: `BRANCHING FACTOR 3`

#### Parser commands

File can contain the following commands:

- **P**rinting the tree:
    - `P`. Just `P`.
    - Example: `P`
- **I**nsertion:
  - `I XXX YYY`, where `XXX` is a key (rational number) and `YYY` is a string value
  - Example: `I 2022/2 The worst month`
- **D**eletion:
  - `D XXX`, where `XXX` is the key to delete
  - Example: `D 2022/2`
- **G**etting the value by key:
    - `G XXX`, where `XXX` is the key to get
    - Example: `G 2022/2` will print `The worst month`
  
#### Random operations
  
- **I**nsertion of the **R**andom keys:
    - Inserts random keys with value `R`
    - `IR XXX/YYY`, where `XXX` is the amount of keys and `YYY` is the max possible key
    - Example: `IR 20/999` will insert 20 keys (or less if the keys can't be unique) that are positive but less than 999



- **D**eletion of the **R**andom keys:
  - Deletes random keys. If there is nothing to delete, stops.
  - `DR XXX`, where `XXX` is the amount of keys to delete
  - Example: `DR 10` will delete 10 keys (or less if there are less than 10 keys in the tree)

