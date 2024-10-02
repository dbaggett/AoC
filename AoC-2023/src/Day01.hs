module Day01 (solution) where

import Data.List
import Data.List.Split
import Data.Char (isNumber, digitToInt)

firstLast :: String -> Int
firstLast s = read [head s, last s]

partOne :: String -> Int
partOne = sum . map (firstLast . filter isNumber) . lines

numberString :: String -> String
numberString [] = []
numberString s
    | take 3 s == "one" = '1' : numberString (tail s)
    | take 3 s == "two" = '2' : numberString (tail s)
    | take 5 s == "three" = '3' : numberString (tail s)
    | take 4 s == "four" = '4' : numberString (tail s)
    | take 4 s == "five" = '5' : numberString (tail s)
    | take 3 s == "six" = '6' : numberString (tail s)
    | take 5 s == "seven" = '7' : numberString (tail s)
    | take 5 s == "eight" = '8' : numberString (tail s)
    | take 4 s == "nine" = '9' : numberString (tail s)
    | otherwise = head s : numberString (tail s)

solution :: String -> IO ()
solution input = do
  let part1 = partOne input
  let part2 = partOne $ numberString input

  putStrLn $ "Day 1: " ++ show part1
  putStrLn $ "Day 2: " ++ show part2