module Main where

import qualified Data.Map as Map
import qualified Day01 (solution)
import qualified Day02 (solution)
import qualified Day03 (solution)
import qualified Day04 (solution)
import qualified Day05 (solution)
import qualified Day06 (solution)
import qualified Day07 (solution)
import qualified Day08 (solution)
import qualified Day09 (solution)
import qualified Day10 (solution)
import qualified Day11 (solution)
import qualified Day13 (solution)
import qualified Day14 (solution)
import System.Environment (getArgs)

solutions =
  Map.fromList
    [ ("01", Day01.solution),
      ("02", Day02.solution),
      ("03", Day03.solution),
      ("04", Day04.solution),
      ("05", Day05.solution),
      ("06", Day06.solution),
      ("07", Day07.solution),
      ("08", Day08.solution),
      ("09", Day09.solution),
      ("10", Day10.solution),
      ("11", Day11.solution),
      ("13", Day13.solution),
      ("14", Day14.solution)
    ]

solveSingle :: String -> IO ()
solveSingle s = case Map.lookup s solutions of
  Just f -> readFile (concat ["./data/Day", s, ".txt"]) >>= f
  Nothing -> putStrLn $ "Day not implemented: " ++ s

solveProblems :: [String] -> IO ()
solveProblems = mapM_ solveSingle

main :: IO ()
main = do
  args <- getArgs
  if null args
    then solveProblems $ Map.keys solutions
    else solveProblems args
