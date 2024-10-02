module Day04 (solution) where
import Data.List.Split (splitOn)
import Data.Char (isNumber, isSpace)
import Data.Map (Map, fromList, (!), adjust, toList)

data Card = Card CardId [Int] [Int] deriving (Show)

type CardId = Int
type CardCount = Int
type Offset = CardId

trim :: String -> String
trim = f . f
   where f = reverse . dropWhile isSpace

intersect :: [Int] -> [Int] -> [Int]
intersect [] = const []
intersect xs = filter (`elem` xs)

parseCard :: [String] -> Card
parseCard card = Card id winningNumbers scratchedNumbers
  where
    id = read $ filter isNumber $ head . splitOn ":" $ head card
    winningNumbers = map read . head . map (words . trim) . tail . splitOn ":" $ head card
    scratchedNumbers = map read . head . map (words . trim) $ tail card

runningDoubles :: [Int] -> Int
runningDoubles n = fst $ doublePairs (0, n)
  where
    doublePairs :: (Int, [Int]) -> (Int, [Int])
    doublePairs (x, []) = (x + 0, [])
    doublePairs (x, y)
      | x <= 0 = doublePairs (x + 1, tail y)
      | otherwise = doublePairs (x * 2, tail y)

cardZip :: [Card] -> [(CardId, Int)]
cardZip = map (\(Card id _ _) -> (id, 1))

partOne :: [Card] -> Int
partOne = sum . map (\(Card _ w s) -> runningDoubles $ intersect w s)

partTwo :: [Card] -> Int
partTwo cards = sum . map snd . toList . foldl go startingState $ cards
    where startingState = fromList . cardZip $ cards
          go m (Card id w s) = foldl (flip $ adjust (+ (m ! id))) m [id + 1 .. id + length (w `intersect` s)]

solution :: String -> IO()
solution input = do
  let cards = map (parseCard . splitOn "|") $ lines input

  putStrLn $ "Part 1: " ++ show (partOne cards)
  putStrLn $ "Part 2: " ++ show (partTwo cards)