#!/usr/bin/env python3
import random
import math

def generate_non_square_environment(filename, width, height, num_dirty_cells, num_blocked_cells):
    grid = [['-' for _ in range(width)] for _ in range(height)]

    # Place dirty cells randomly
    for _ in range(num_dirty_cells):
        x, y = random.randint(0, width - 1), random.randint(0, height - 1)
        while grid[y][x] == 'D':
            x, y = random.randint(0, width - 1), random.randint(0, height - 1)
        grid[y][x] = 'D'

    # Place blocked cells randomly
    for _ in range(num_blocked_cells):
        x, y = random.randint(0, width - 1), random.randint(0, height - 1)
        while grid[y][x] == 'D' or grid[y][x] == 'X':
            x, y = random.randint(0, width - 1), random.randint(0, height - 1)
        grid[y][x] = 'X'

    # Write the grid to the file
    with open(filename, 'w') as f:
        for row in grid:
            f.write(''.join(row) + '\n')

if __name__ == "__main__":
    filename = "./environments/non_square_environment.txt"  # Specify the output filename
    width = 40 # Adjust the width of the environment
    height = 4  # Adjust the height of the environment
    blocked_percent = 0.20
    num_blocked_cells = math.floor(width * height * blocked_percent)  # Number of blocked cells to place
    num_dirty_cells = (width * height) - num_blocked_cells  # Number of dirty cells to place

    generate_non_square_environment(filename, width, height, num_dirty_cells, num_blocked_cells)
    print(f"Non-square environment saved to {filename}")

