#!/usr/bin/env python3

import random
import math


def generate_environment(filename, grid_size, num_dirty_cells, num_blocked_cells):
    grid = [['-' for _ in range(grid_size)] for _ in range(grid_size)]

    # Place blocked cells randomly
    for _ in range(num_blocked_cells):
        x, y = random.randint(0, grid_size - 1), random.randint(0, grid_size - 1)
        while grid[y][x] == 'D' or grid[y][x] == 'X':
            x, y = random.randint(0, grid_size - 1), random.randint(0, grid_size - 1)
        grid[y][x] = 'X'

    # Make sure there are no unreachable cells
    for y in range(grid_size):
        for x in range(grid_size):
            if grid[y][x] == 'X':
                if y > 0 and grid[y - 1][x] == '-':
                    grid[y - 1][x] = 'X'
                if y < grid_size - 1 and grid[y + 1][x] == '-':
                    grid[y + 1][x] = 'X'
                if x > 0 and grid[y][x - 1] == '-':
                    grid[y][x - 1] = 'X'
                if x < grid_size - 1 and grid[y][x + 1] == '-':
                    grid[y][x + 1] = 'X'

    # Place dirty cells randomly
    for _ in range(num_dirty_cells):
        x, y = random.randint(0, grid_size - 1), random.randint(0, grid_size - 1)
        while grid[y][x] == 'D':
            x, y = random.randint(0, grid_size - 1), random.randint(0, grid_size - 1)
        grid[y][x] = 'D'

    # Write the grid to the file
    with open(filename, 'w') as f:
        for row in grid:
            f.write(''.join(row) + '\n')

if __name__ == "__main__":
    # Generate 10 square environments with no obstacles, from 3x3
    for i in range(3, 11):
        filename = f"./environments/square/square_environment{i}x{i}.txt"
        num_dirty_cells = i * i
        generate_environment(filename, i, num_dirty_cells, 0)
        print(f"Environment saved to {filename}")

    # Generate 10 m x n environments with no obstacles, where m != n and m, n < 30
    count = 0;
    while count < 10:
        m = random.randint(3, 30)
        n = random.randint(3, 30)
        if m != n:
            filename = f"./environments/nonsquare/nonsquare_environment{m}x{n}.txt"
            num_dirty_cells = m * n
            num_blocked_cells = math.floor(m * n * 0.2)
            generate_environment(filename, m, num_dirty_cells, num_blocked_cells)
            print(f"Environment saved to {filename}")
            count += 1
