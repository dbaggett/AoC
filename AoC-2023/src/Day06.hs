module Day06 (solution) where

worldRecords :: (Int, Int) -> Int
worldRecords (t, d) = foldl (\wins speed -> (if (t - speed) * speed > d then wins + 1 else wins)) 0 [t - 1, t - 2 .. 1]

splitReduce :: [(Int, Int)] -> (Int, Int)
splitReduce input = (first, second)
  where
    first = read $ concatMap (show . fst) input
    second = read $ concatMap (show . snd) input

parseInput :: String -> [(Int, Int)]
parseInput input = zip times distances
  where
    components = map words $ lines input
    times = map read (tail $ head components)
    distances = map read (tail $ head $ tail components)

solution :: String -> IO ()
solution input = do
  putStrLn $ "Part 1: " ++ show (product $ map worldRecords $ parseInput input)
  putStrLn $ "Part 2: " ++ show (worldRecords $ splitReduce (parseInput input))