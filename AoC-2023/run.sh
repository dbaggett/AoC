#!/bin/bash

# FILEPATH: /Users/danny.baggett/projects/AOC-2023/run.sh

directory=$(printf "Day_%02d" $@)
source_file=$(printf "$directory/day_%02d.hs" $@)
executable_file=$(printf "$directory/day_%02d" $@)
object_file=$(printf "$directory/day_%02d.o" $@)
header_file=$(printf "$directory/day_%02d.hi" $@)

# Compile Haskell file
ghc $source_file

# Execute binary with command line arguments
./$executable_file

# Clean up compilation files
rm $executable_file $header_file $object_file
