# B+ tree

**This is the Java implementation of B+ tree structure.**

It supports inserting, getting and deleting programmatically.

### File parser

The file parser is used for testing and demonstration purposes. 
It creates a tree (or uses one), 
the **keys** of which are of the type `Integer`, 
and the **values** of which are of the type `String`.
It can parse the commands from the text file.

To successfully parse a file, the file should contain the following in its first line:
```text 
BRANCHING FACTOR XXX
```
, where `XXX` is the branching factor (maximum amount of children a node can have) of the tree.

#### Parser commands

File can contain the following commands:

- **P**rinting the tree:
    - `P`. Just `P`.
    - Example: `P`
  
- **G**etting the value by key:
    - `G XXX`, where `XXX` is the key to get
    - Example: `G 2022` will print `The worst year`
  
- **I**nsertion:
  - `I XXX YYY`, where `XXX` is an integer key and `YYY` is a string value
  - Example: `I 2022 The worst year`
  
- **I**nsertion of the **R**andom keys:
    - Inserts random keys with value `R`
    - `IR XXX YYY`, where `XXX` is the amount of keys and `YYY` is the max possible key
    - Example: `IR 20 999` will insert 20 keys (or less if the keys can't be unique) that are positive but less than 999

- **D**eletion:
  - `D XXX`, where `XXX` is the key to delete
  - Example: `D 2022`

- **D**eletion of the **R**andom keys:
  - Deletes random keys. If there is nothing to delete, stops.
  - `DR XXX`, where `XXX` is the amount of keys to delete
  - Example: `DR 10` will delete 10 keys (or less if there are less than 10 keys in the tree)

