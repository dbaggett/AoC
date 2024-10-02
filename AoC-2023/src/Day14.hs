module Day14 (solution) where
import Data.List.Split (splitOn)
import Data.List (sortBy, intercalate, transpose)
import Data.Ord (comparing, Down(..))

parseInput :: String -> [String]
parseInput = map concat . transpose . map (map (: [])) . lines

bubbleUpAndNumber :: [String] -> [[(Int, Char)]]
bubbleUpAndNumber input = map (anchor . orderedSplits) input
 where
  orderedSplits = map (sortBy (comparing Data.Ord.Down)) . splitOn "#"
  rows = length $ head input
  anchor = zip [rows, (rows - 1)..1] . intercalate "#"

partOne :: [[(Int, Char)]] -> Int
partOne input = sum $ map (sum . map fst) filtered
 where
  filtered = map (filter (\(i, c) -> c == 'O')) input

solution :: String -> IO ()
solution input = do
  putStrLn $ "Part 1: " ++ show (partOne . bubbleUpAndNumber $ parseInput input)