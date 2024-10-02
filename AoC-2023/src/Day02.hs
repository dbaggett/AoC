module Day02 (solution) where
import Data.List.Split (splitOn, split)
import Data.Char (isDigit, isSpace)
import Text.Regex.TDFA ((=~))


data Color = Red Int | Blue Int | Green Int deriving (Show, Read)

type Round = [Color]

data Game = Game Int [Round] deriving (Show, Read)

newGame :: (String, String) -> Game
newGame game = Game (read . filter isDigit $ fst game) (map (map newColor . matchColor) $ splitOn ";" $ snd game)
  where
    matchColor :: String -> [[String]]
    matchColor s = s =~ "([0-9]+) (red|green|blue)" :: [[String]]
    newColor [_, n, "red"] = Red (read n)
    newColor [_, n, "green"] = Green (read n)
    newColor [_, n, "blue"] = Blue (read n)
    newColor _ = Blue 42

tuplify :: [a] -> (a, a)
tuplify [x, y] = (x, y)

partOne :: [Game] -> Int
partOne = sum . map gameId . filter validGame
  where
    gameId (Game id _) = id
    validGame (Game _ rounds) = all validRound rounds
    validColor (Red n) = n <= 12
    validColor (Green n) = n <= 13
    validColor (Blue n) = n <= 14
    validRound = all validColor

partTwo :: [Game] -> Int
partTwo = sum . map ((\color -> requiredBlue color * requiredRed color * requiredGreen color) . concat . rounds)
  where
    rounds (Game _ rounds) = rounds
    isBlue (Blue _) = True
    isBlue _ = False
    isRed (Red _) = True
    isRed _ = False
    isGreen (Green _) = True
    isGreen _ = False
    requiredBlue = maximum . map (\(Blue  n) -> n) . filter isBlue
    requiredRed = maximum . map (\(Red  n) -> n) . filter isRed
    requiredGreen = maximum . map (\(Green  n) -> n) . filter isGreen

solution :: String -> IO ()
solution input = do
  let games = map (newGame . tuplify . splitOn ":") $ lines input

  let part1 = partOne games
  let part2 = partTwo games

  putStrLn $ "Day 1: " ++ show part1
  putStrLn $ "Day 2: " ++ show part2