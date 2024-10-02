module Day10 (solution) where
import Data.Bifunctor (bimap)
import Data.Tuple.Extra (both)
import Data.Matrix (Matrix, fromLists, (!), ncols, nrows)
import Data.Set (Set, empty, insert, member, notMember)

data Heading = North | East | South | West | Unkown deriving (Show, Eq, Ord, Enum)

data Pipe a b = Empty | Vertical a b | Horizontal a b | L a b | J a b | Seven a b | F a b | S a deriving (Show, Eq, Ord)

type Game = ((Int, Int), Matrix (Pipe Heading Heading))

toPipe raw = case raw of
  '|' -> Vertical North South
  '-' -> Horizontal West East
  '.' -> Empty
  'L' -> L North East
  'J' -> J North West
  '7' -> Seven South West
  'F' -> F South East
  'S' -> S Unkown
  _   -> error "Invalid pipe"

dfs :: Game -> Set (Int, Int)
dfs (sLocation, grid) = dfs' [sLocation] empty
 where
  dfs' [] visited = visited
  dfs' (current : rest) visited
    | current `elem` visited = dfs' rest visited
    | otherwise              = dfs' (neighbors ++ rest) (insert current visited)
   where
    tupleDiff (r, c) = uncurry bimap (both (-) current) (r, c)
    heading (r, c) = case tupleDiff (r, c) of
      (0, 1)  -> West
      (0, -1) -> East
      (1, 0)  -> North
      (-1, 0) -> South
    neighbors = filter (/= current) . filter (`notMember` visited) $ getNeighbors current
    pipe (r, c) = grid ! (r, c)
    rightWay (r, c) = case pipe (r, c) of
      Vertical _ _   -> heading (r, c) == North || heading (r, c) == South
      Horizontal _ _ -> heading (r, c) == West || heading (r, c) == East
      L _ _          -> heading (r, c) == South || heading (r, c) == West
      J _ _          -> heading (r, c) == South || heading (r, c) == East
      Seven _ _      -> heading (r, c) == North || heading (r, c) == East
      F _ _          -> heading (r, c) == North || heading (r, c) == West
      S _            -> True
      Empty          -> False
    rows = nrows grid
    cols = ncols grid
    inBounds (row, col) = 0 < row && row <= rows && 0 < col && col <= cols
    getNeighbors (r, c) = filter rightWay $ case pipe (r, c) of
      Vertical _ _ -> filter inBounds [(r - 1, c), (r + 1, c)]
      Horizontal _ _ -> filter inBounds [(r, c - 1), (r, c + 1)]
      L _ _ -> filter inBounds [(r, c + 1), (r - 1, c)]
      J _ _ -> filter inBounds [(r, c - 1), (r - 1, c)]
      Seven _ _ -> filter inBounds [(r, c - 1), (r + 1, c)]
      F _ _ -> filter inBounds [(r, c + 1), (r + 1, c)]
      S _ -> filter inBounds [(r, c - 1), (r, c + 1), (r + 1, c), (r - 1, c)]

partOne :: Game -> Int
partOne = (`div` 2) . length . dfs

parseInput :: String -> Game
parseInput input = (sLocation, fromLists raw)
 where
  raw = map (map toPipe) $ lines input
  sLocation = head $ filter (\(r, c) -> raw !! (r - 1) !! (c - 1) == S Unkown) [(r, c) | r <- [1 .. length raw], c <- [1 .. length (head raw)]]

solution :: String -> IO ()
solution input = do
  putStrLn $ "Part 1: " ++ show (partOne $ parseInput input)