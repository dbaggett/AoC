module Day03 (solution) where
import Data.List
import Data.Matrix (Matrix, fromLists, nrows, ncols, (!))
import Data.Char (isDigit)

data PartNumber = PartNumber { value :: Int, row :: Int, span :: (Int, Int) } deriving (Show)

parseInput :: String -> (Matrix Char, [PartNumber], [(Int, Int)])
parseInput input = (matrix, partNumbers, gears)
  where
    raw = lines input
    matrix = fromLists raw
    numberedColumns = map (zip [1..]) raw
    numberGroupings = map (groupBy (\x y -> isDigit (snd x) == isDigit (snd y))) numberedColumns
    locationAndCharacters = map (map (\l -> ((fst $ head l, fst $ last l), map snd l))) numberGroupings
    numberedRows  = zip [1 .. ] $ map (filter (\(_, n) -> all isDigit n)) locationAndCharacters
    partNumbers = concatMap (\(r, row) -> map (\(s, n) -> PartNumber (read n) r s) row) numberedRows
    gearCols = map (filter ((== '*') . snd )) numberedColumns
    gearRows = zip [1 .. ] gearCols
    gears = concatMap (\(r, row) -> map (\(c, _) -> (r, c)) row) gearRows

partOne :: (Matrix Char, [PartNumber], [(Int, Int)]) -> Int
partOne (matrix, partNumbers, _) = sum . map value . filter adjacent $ partNumbers
  where
    numberRows = nrows matrix
    numberColumns = ncols matrix
    neighbors (PartNumber _ r (c1, c2)) = [matrix ! (i, j) |
                                             i <- [r - 1 .. r + 1],
                                             0 < i && i <= numberRows,
                                             j <- [c1 - 1 .. c2 + 1],
                                             0 < j && j <= numberColumns]
    adjacent = any (\x -> not (isDigit x) && x /= '.') . neighbors

partTwo :: (Matrix Char, [PartNumber], [(Int, Int)]) -> Int
partTwo (grid, numbers, gears) = sum . map (product . map value) . filter ((== 2) . length) . map getNeighbours $ gears
    where maxRow = nrows grid
          maxCol = ncols grid
          getNeighboursPos (r, c) = [(i, j) | i <- [r - 1 .. r + 1], j <- [c - 1 .. c + 1], 0 < i && i <= maxRow, 0 < j && j <= maxCol]
          getNeighbours g = filter (\(PartNumber _ r (c1, c2)) -> any (`elem` neighbours) [(r, c) | c <- [c1 .. c2]]) numbers where neighbours = getNeighboursPos g

solution :: String -> IO ()
solution input = do

  let part1 = partOne $ parseInput input
  let part2 = partTwo $ parseInput input

  putStrLn $ "Day 1: " ++ show part1
  putStrLn $ "Day 2: " ++ show part2