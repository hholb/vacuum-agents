#!/usr/bin/env python3

import random

def generate_environment(filename, grid_size, num_dirty_cells, num_blocked_cells):
    grid = [['-' for _ in range(grid_size)] for _ in range(grid_size)]

    # Place dirty cells randomly
    for _ in range(num_dirty_cells):
        x, y = random.randint(0, grid_size - 1), random.randint(0, grid_size - 1)
        while grid[y][x] == 'D':
            x, y = random.randint(0, grid_size - 1), random.randint(0, grid_size - 1)
        grid[y][x] = 'D'

    # Place blocked cells randomly
    for _ in range(num_blocked_cells):
        x, y = random.randint(0, grid_size - 1), random.randint(0, grid_size - 1)
        while grid[y][x] == 'D' or grid[y][x] == 'X':
            x, y = random.randint(0, grid_size - 1), random.randint(0, grid_size - 1)
        grid[y][x] = 'X'

    # Write the grid to the file
    with open(filename, 'w') as f:
        for row in grid:
            f.write(''.join(row) + '\n')

if __name__ == "__main__":
    filename = "./environments/environment.txt"  # Specify the output filename
    grid_size = 10  # Adjust the grid size as needed
    num_dirty_cells = 10  # Number of dirty cells to place
    num_blocked_cells = 10  # Number of blocked cells to place

    generate_environment(filename, grid_size, num_dirty_cells, num_blocked_cells)
    print(f"Environment saved to {filename}")

