{-# LANGUAGE OverloadedStrings #-}
module Day07 (solution) where
import Data.Map (Map, (!), fromList)
import Data.List.Split (splitOn)
import Data.List (group, sortBy, sort, groupBy, elemIndex)
import Data.List.Utils (countElem)
import Data.Text (pack, unpack, replace)
import Text.Regex.TDFA (RegexContext(match))

ranks :: Bool -> [Char]
ranks False = ['2' .. '9'] ++ ['T', 'J', 'Q', 'K', 'A']
ranks True = ['J'] ++ ['2' .. '9'] ++ ['T', 'Q', 'K', 'A']

data Hand = Hand { cards :: [Char], bid :: Int, hType :: HandType, jokersWild :: Bool } deriving (Show, Eq)
data HandType = HighCard
              | OnePair
              | TwoPair
              | ThreeOfAKind
              | FullHouse
              | FourOfAKind
              | FiveOfAKind deriving (Show, Eq, Ord)

-- thanks (or no thanks) to the grouping by HandType, I don't need to compare on HandType
instance Ord Hand where
  (Hand c1 _ _ b) `compare` (Hand c2 _ _ _) = uncurry compare $ mapTuple (map (cardStrength !)) (c1, c2)
    where
      cardStrength = fromList $ zip (ranks b) [1 .. ]

mapTuple :: (a -> b) -> (a, a) -> (b, b)
mapTuple f (a, b) = (f a, f b)

handTypeRank :: HandType -> Int
handTypeRank hType = case hType of
  HighCard -> 1
  OnePair -> 2
  TwoPair -> 3
  ThreeOfAKind -> 4
  FullHouse -> 5
  FourOfAKind -> 6
  FiveOfAKind -> 7

parseInput :: (Bool, String) -> [Hand]
parseInput input = map ((\[cards, bid] -> Hand cards (read bid) (handType cards) (fst input)) . words) (lines $ snd input)
  where
    handType cards = matchType $ sortBy (\a b -> compare (length b) (length a) ) $ group $ sort cards
    matchType :: [[Char]] -> HandType
    matchType (a:b:_) | length a == 3 && length b == 2 = FullHouse
                          | length a == 2 && length b == 3 = FullHouse
                          | length a == 4 = FourOfAKind
                          | length a == 3 = ThreeOfAKind
                          | length a == 2 && length b == 2 = TwoPair
                          | length a == 2 = OnePair
                          | otherwise = HighCard
    matchType _ = FiveOfAKind

matchType :: [Char] -> HandType
matchType = getHandType . sort . map length . group . sort
    where getHandType [5] = FiveOfAKind
          getHandType [1, 4] = FourOfAKind
          getHandType [2, 3] = FullHouse
          getHandType [1, 1, 3] = ThreeOfAKind
          getHandType [1, 2, 2] = TwoPair
          getHandType [1, 1, 1, 2] = OnePair
          getHandType _ = HighCard

promotedType :: String -> HandType
promotedType s = maximum . map (matchType . upgrade) $ "23456789TQKA"
    where
      upgrade c = unpack . replace "J" (pack [c]) . pack $ s

partOne :: [Hand] -> Int
partOne = sum . zipWith (*) [1 .. ] . map bid . concatMap sort . groupBy (\a b -> hType a == hType b) . sortBy (\a b -> compare (handTypeRank $ hType a) (handTypeRank $ hType b))

partTwo :: [Hand] -> Int
partTwo = partOne . map (\hand -> hand { hType = promotedType $ cards hand })

solution :: String -> IO()
solution input = do
  putStrLn $ "Part 1: " ++ show (partOne $ parseInput (False, input))
  putStrLn $ "Part 2: " ++ show (partTwo $ parseInput (True, input))