module Day13 (solution) where
import Data.List (transpose, sortBy)
import Data.List.Split (splitOn)

data Mirror = Mirror { horizontal :: [[String]], vertical :: [[String]] } deriving (Show, Eq)

splitRow :: String -> [String]
splitRow = map (: [])

reduceTuples :: [(Int, Int)] -> (Int, Int)
reduceTuples = foldr (\(x, y) (accX, accY) -> (x + accX, y + accY)) (0, 0)

reflections :: [Mirror] -> Int
reflections mirrors = uncurry (+) . reduceTuples $ map reflections' mirrors
  where
    reflections' mirror = (100 * hCount, vCount)
      where
        reflectionPointsH = reflectionPoints planeH
        reflectionPointsV = reflectionPoints planeV
        planeH = horizontal mirror
        planeV = vertical mirror
        spanCount plane pairs | not (null pairs) && ((fst . head) pairs == 1 || (snd . head) pairs == length plane) = (fst . last) pairs
        spanCount _ _ = 0
        hCount = spanCount planeH reflectionPointsH
        vCount = spanCount planeV reflectionPointsV
    flattenToList tuples = listA ++ reverse listB
      where
        listA = map fst tuples
        listB = map snd tuples
    contiguousOnly points | shortBuildRange points == flattenToList points = points
    contiguousOnly _ = []
    reflectionPoints :: [[String]] -> [(Int, Int)]
    reflectionPoints plane = contiguousOnly [(i, j) | (i, row1) <- zip [1..] plane, (j, row2) <- zip [1..] plane, i < j, row1 == row2]
    shortBuildRange :: [(Int, Int)] -> [Int]
    shortBuildRange points | not (null points) = [(fst $ head points)..(snd $ head points)]
    shortBuildRange [] = []

parseInput :: String -> [Mirror]
parseInput input = parseMirrors
  where
    boards = map (map splitRow . lines) $ splitOn "\n\n" input
    parseMirror board = Mirror board (transpose board)
    parseMirrors = map parseMirror boards

solution :: String -> IO ()
solution input = do
  testInput <- readFile "data/day13-test.txt"
  putStrLn $ "\tPart 1: " ++ show (reflections $ parseInput testInput)