module Day09 (solution) where
import Topograph (pairs)
import Data.Tuple (swap)

differences :: [Int] -> [Int]
differences xs = map (uncurry (-) . swap) $ pairs xs

parseInput :: String -> [[Int]]
parseInput input = map (map read . words) $ lines input

subHistories :: [[Int]] -> [[Int]]
subHistories [xs] | not . all (==0) $ xs = xs : subHistories [differences xs]
subHistories [xs] = [xs]

partOne :: [[Int]] -> Int
partOne history = sum $ map subSum history
  where
    subSum numbers = sum $ map last (subHistories [numbers])

partTwo :: [[Int]] -> Int
partTwo history = sum $ map subSum history
  where
    subSum numbers = foldr ((-) . head) 0 $ subHistories [numbers]

solution :: String -> IO ()
solution input = do
  putStrLn $ "Part 1: " ++ show (partOne $ parseInput input)
  putStrLn $ "Part 2: " ++ show (partTwo $ parseInput input)
