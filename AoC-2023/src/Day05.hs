module Day05 (solution) where
import Data.List.Split (splitOn)

data GardeningMap = GardeningMap { start :: Int
                                 , end :: Int
                                 , rangeOffset :: Int
                                 } deriving (Show)

parseInput :: String -> ([Int], [[GardeningMap]])
parseInput input = (seeds, mappings)
  where
    [seedSeg]:mappingsSeg = map lines $ splitOn "\n\n" input
    seeds = map read . words . concat . tail $ splitOn ": " seedSeg
    mappings = map (map (createMap . map read . words) . tail) mappingsSeg
    createMap [destination, source, range] = GardeningMap source (source + range) (destination - source)

findMap :: Int -> [GardeningMap] -> Int
findMap source smap | not . null $ lookup = source + (rangeOffset . head $ lookup)
                    | otherwise           = source
                    where lookup = filter (\(GardeningMap s e _) -> s <= source && source < e) smap

partOne :: ([Int], [[GardeningMap]]) -> Int
partOne (seeds, maps) = minimum . map (\x -> foldl findMap x maps) $ seeds

solution :: String -> IO ()
solution input = do


  putStrLn $ "Part 1: " ++ show (partOne $ parseInput input)
