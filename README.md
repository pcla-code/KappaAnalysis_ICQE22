# KappaAnalysis
Analyzing how reliable Kappa is with smaller sub samples using Monte Carlo Analysis

### Introduction
Cohen’s Kappa has been used in interrater reliability calculation for decades, often for small samples. Recently, researchers within the quantitative ethnography community have argued that Cohen’s Kappa cannot validly be used without much larger samples (Eagan et al., 2017, 2020)

### Table of Contents
* [Technologies Used](#technologies-used)
* Main
* TableBuilder
* csvWriter

### Technologies Used
* Java 8
* [XYChart Library](https://knowm.org/open-source/xchart/#:~:text=XChart%20is%20a%20light%2Dweight,save%20it%20or%20display%20it)

### Main
* Sets up our JFrame to showcase the graphs to display
* Iterate through each kappa value from 0.6 - 0.8, create a simulated dataset, and run a Monte Carlo Simulation on it.

### TableBuilder
* Builds a 2x2 table, initialized with random numbers (either 0 or 1), to as precise as possible, hit a target Kappa
  * Does so by randomlly setting the first 200 values, then selectively setting the remaining population size to match the target kappa
* Additionally, this class also has the function, getKappaValue, which allows us to get the table's Kappa
* Parameters: Population Size, and target kappa

### csvWriter
* Creates a file named the current time
* In the file includes each timestamp of Monte Carlo Analysis Kappa value compared to the criterion kappa (e.g. 0.8)
