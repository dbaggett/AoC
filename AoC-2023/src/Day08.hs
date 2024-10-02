module Day08 (solution) where
import Data.List.Split (splitOn)
import Text.Regex.TDFA
import Data.List.Utils (replace)
import Data.Map (Map, fromList, (!), keys)
import Data.List

type Game = (String, Map String (String, String))

parseInput :: String -> Game
parseInput input = (cycle instructions, fromList mappings)
  where
    instructions:rest = splitOn "\n\n" input
    rawMappings = map (\l -> tail $ head (l =~ "([A-Z]{3}) = \\(([A-Z]{3}), ([A-Z]{3})\\)" :: [[String]])) . lines $ head rest
    mappings = map (\[key, left, right] -> (key, (left, right))) rawMappings

partOne :: Game -> Int
partOne (instructions, mappings) = fst . last $ takeWhile (\r -> "ZZZ" /= snd r) . scanl toNext (1, "AAA") $ instructions
  where
    toNext (acc, key) 'L' = (acc + 1, fst $ mappings ! key)
    toNext (acc, key) 'R' = (acc + 1, snd $ mappings ! key)
    toNext (acc, key) _ = (acc, key)

partTwo :: Game -> Int
partTwo (instructions, mappings) = foldl1 lcm $ map traverse initials
  where
    initials = filter ("A" `isSuffixOf`) $ keys mappings
    traverse entry = fst . last $ takeWhile (not . ("Z" `isSuffixOf`) . snd) . scanl toNext (1, entry) $ instructions
    toNext (acc, key) 'L' = (acc + 1, fst $ mappings ! key)
    toNext (acc, key) 'R' = (acc + 1, snd $ mappings ! key)
    toNext (acc, key) _ = (acc, key)

solution :: String -> IO ()
solution input = do
  putStrLn $ "Part 1: " ++ show (partOne $ parseInput input)
  putStrLn $ "Part 2: " ++ show (partTwo $ parseInput input)