module Day11 (solution) where
import Data.List (permutations, sort, nub, elemIndex, intersect)

type Galaxy = (Int, Int)
data Universe = Universe { galaxies :: [Galaxy], emptyRows :: [Int], emptyColumns :: [Int] } deriving (Show, Eq)

splitRow :: String -> [String]
splitRow = map (: [])

expandGalaxies :: Universe -> Int -> [Galaxy]
expandGalaxies (Universe galaxies er ec) multiplier = map expand galaxies
  where
    expand (row, col) = (row + (multiplier - 1) * rInflation, col + (multiplier - 1) * cInflation)
      where
        rInflation = length $ filter (< row) er
        cInflation = length $ filter (< col) ec

universeExpansion :: Int -> Universe -> Int
universeExpansion multiplier universe = sum [distance g1 g2 | g1 <- galaxies, g2 <- galaxies, g1 < g2]
  where
    galaxies = expandGalaxies universe multiplier
    distance (x1, y1) (x2, y2) = abs (x2 - x1) + abs (y2 - y1)

parseInput :: String -> Universe
parseInput input = Universe galaxies emptyRows emptyCols
  where
    rows = lines input
    origGrid = map splitRow rows
    origRowCount = length origGrid
    origColCount = length $ head origGrid
    origNumberedCols = map (zip [1..origRowCount]) origGrid
    filterEmptyColNumsPerRow = map (map fst . filter (\(_, x) -> x == ".")) origNumberedCols
    emptyRows = map fst . filter (\x -> length (snd x) == origColCount) $ zip [1..origRowCount] filterEmptyColNumsPerRow
    emptyCols = foldl1 intersect filterEmptyColNumsPerRow
    galaxies = [(i, j) | (i, row) <- zip [1 .. ] rows, (j, char) <- zip [1 .. ] row, char == '#']

solution :: String -> IO ()
solution input = do
  putStrLn $ "Part 1: " ++ show (universeExpansion 2 (parseInput input))
  putStrLn $ "Part 2: " ++ show (universeExpansion 1000000 (parseInput input))