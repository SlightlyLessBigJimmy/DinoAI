"""
Snake AI Evolution System
-------------------------

This program trains snakes using a Genetic Algorithm (Neuroevolution).

Each snake:
- Has a small neural network "brain"
- Takes in information about its environment
- Outputs a movement decision
- Is evaluated using a fitness function

The best snakes reproduce (with mutation),
creating the next generation.

Features:
- Parallel population simulation
- Save/load trained snakes (with generation number)
- Resume training from loaded model
- Test mode (single snake, no evolution)
- Adjustable simulation speed
"""

import pygame
import random
import numpy as np
import sys
import pickle
import os

# ======================
# CONFIGURATION
# ======================

WIDTH, HEIGHT = 800, 800        # Window size
GRID_SIZE = 20                  # Size of one snake segment
ROWS = WIDTH // GRID_SIZE       # Number of grid cells per row

POPULATION_SIZE = 200           # Number of snakes per generation
MUTATION_RATE = 0.05            # Chance each weight mutates
ELITE_PERCENT = 0.1             # Top % preserved each generation
MAX_STEPS = 300                 # Prevent infinite survival

SAVE_FILE = "best_snake.pkl"    # File for saving trained model

# Initialize pygame
pygame.init()
screen = pygame.display.set_mode((WIDTH, HEIGHT))
pygame.display.set_caption("Snake AI Evolution")
clock = pygame.time.Clock()
font = pygame.font.SysFont("arial", 18)

# Simulation control variables
simulation_speed = 1.0
MIN_SPEED = 0.25
MAX_SPEED = 200

test_mode = False               # If True: no evolution
loaded_brain = None
loaded_generation = 1


# ======================
# NEURAL NETWORK
# ======================

class NeuralNet:
    """
    A very simple 2-layer neural network.

    Input layer: 8 neurons
    Hidden layer: 16 neurons
    Output layer: 4 neurons (movement decisions)
    """

    def __init__(self):
        # Randomly initialize weights
        # Shape: (input_size, hidden_size)
        self.w1 = np.random.randn(8, 16)

        # Shape: (hidden_size, output_size)
        self.w2 = np.random.randn(16, 4)

    def forward(self, x):
        """
        Performs a forward pass through the network.

        x → hidden layer (tanh activation)
        hidden → output layer (raw scores)
        """

        x = np.tanh(np.dot(x, self.w1))  # Activation adds non-linearity
        return np.dot(x, self.w2)

    def clone(self):
        """
        Returns an exact copy of this network.
        Used for reproduction.
        """
        clone = NeuralNet()
        clone.w1 = np.copy(self.w1)
        clone.w2 = np.copy(self.w2)
        return clone

    def mutate(self):
        """
        Randomly modifies some weights.
        This introduces variation into offspring.
        """
        for w in [self.w1, self.w2]:
            mask = np.random.rand(*w.shape) < MUTATION_RATE
            w += mask * np.random.randn(*w.shape) * 0.3


# ======================
# SNAKE AGENT
# ======================

class Snake:
    """
    Represents one AI-controlled snake.
    """

    def __init__(self, brain):
        self.body = [(random.randrange(ROWS), random.randrange(ROWS))]
        self.direction = random.choice([(1,0),(-1,0),(0,1),(0,-1)])
        self.brain = brain
        self.food = self.spawn_food()
        self.alive = True
        self.score = 0
        self.steps = 0

    def spawn_food(self):
        while True:
            pos = (random.randrange(ROWS), random.randrange(ROWS))
            if pos not in self.body:
                return pos

    def get_inputs(self):
        """
        Collects environmental information for the neural network.

        Inputs:
        - Distance to food (x, y)
        - Danger straight
        - Danger left
        - Danger right
        - Current direction (x, y)
        - Normalized body length
        """

        head_x, head_y = self.body[0]

        dx = (self.food[0] - head_x) / ROWS
        dy = (self.food[1] - head_y) / ROWS

        danger_straight = self.is_danger(self.direction)
        danger_left = self.is_danger((-self.direction[1], self.direction[0]))
        danger_right = self.is_danger((self.direction[1], -self.direction[0]))

        return np.array([
            dx, dy,
            danger_straight,
            danger_left,
            danger_right,
            self.direction[0],
            self.direction[1],
            len(self.body)/100
        ])

    def is_danger(self, direction):
        """
        Checks if moving in a given direction causes death.
        """
        x = self.body[0][0] + direction[0]
        y = self.body[0][1] + direction[1]

        if x < 0 or x >= ROWS or y < 0 or y >= ROWS:
            return 1
        if (x,y) in self.body:
            return 1
        return 0

    def update(self):
        """
        Executes one step of the snake:
        - Feed inputs into brain
        - Choose movement
        - Update position
        - Handle collisions
        """

        if not self.alive:
            return

        self.steps += 1

        inputs = self.get_inputs()
        output = self.brain.forward(inputs)
        move = np.argmax(output)

        # Rotate direction based on output
        if move == 1:
            self.direction = (-self.direction[1], self.direction[0])
        elif move == 2:
            self.direction = (self.direction[1], -self.direction[0])

        new_head = (self.body[0][0] + self.direction[0],
                    self.body[0][1] + self.direction[1])

        # Death conditions
        if (new_head[0] < 0 or new_head[0] >= ROWS or
            new_head[1] < 0 or new_head[1] >= ROWS or
            new_head in self.body or
            self.steps > MAX_STEPS):

            self.alive = False
            return

        self.body.insert(0, new_head)

        # Eating food
        if new_head == self.food:
            self.score += 1
            self.food = self.spawn_food()
            self.steps = 0
        else:
            self.body.pop()

    def fitness(self):
        """
        Determines evolutionary success.
        Higher score = more likely to reproduce.
        """
        return self.score * 300 + len(self.body)


# ======================
# EVOLUTION SYSTEM
# ======================

def create_population_from_brain(brain):
    """
    Used when resuming training from a saved model.
    """
    population = [Snake(brain.clone())]

    while len(population) < POPULATION_SIZE:
        new_brain = brain.clone()
        new_brain.mutate()
        population.append(Snake(new_brain))

    return population


def evolve(population):
    """
    Produces next generation using:
    - Selection
    - Elitism
    - Mutation
    """

    population.sort(key=lambda s: s.fitness(), reverse=True)
    next_gen = []

    elite_count = int(POPULATION_SIZE * ELITE_PERCENT)

    # Preserve top performers
    for i in range(elite_count):
        next_gen.append(Snake(population[i].brain.clone()))

    # Fill rest with mutated offspring
    while len(next_gen) < POPULATION_SIZE:
        parent = random.choice(population[:50])
        child_brain = parent.brain.clone()
        child_brain.mutate()
        next_gen.append(Snake(child_brain))

    return next_gen


# ======================
# SAVE / LOAD
# ======================

def save_best(population, generation):
    global loaded_brain, loaded_generation

    best = max(population, key=lambda s: s.fitness())
    loaded_brain = best.brain.clone()
    loaded_generation = generation
    print(loaded_brain.w1)
    print(loaded_brain.w2)
    data = {
        "brain": loaded_brain,
        "generation": generation
    }

    with open(SAVE_FILE, "wb") as f:
        pickle.dump(data, f)


def load_snake():
    global loaded_brain, loaded_generation

    if os.path.exists(SAVE_FILE):
        with open(SAVE_FILE, "rb") as f:
            data = pickle.load(f)

        loaded_brain = data["brain"]
        loaded_generation = data["generation"]

        return Snake(loaded_brain.clone())

    return None


# ======================
# DRAWING
# ======================

def draw(population, generation):
    screen.fill((20,20,20))

    best = max(population, key=lambda s: s.fitness())
    best_apples = best.score

    for snake in population:
        if not snake.alive:
            continue

        snake_color = (0,255,0) if snake == best else (0,80,0)
        apple_color = (255,0,0) if snake == best else (120,0,0)

        for segment in snake.body:
            pygame.draw.rect(screen, snake_color,
                             (segment[0]*GRID_SIZE,
                              segment[1]*GRID_SIZE,
                              GRID_SIZE, GRID_SIZE))

        pygame.draw.rect(screen, apple_color,
                         (snake.food[0]*GRID_SIZE,
                          snake.food[1]*GRID_SIZE,
                          GRID_SIZE, GRID_SIZE))

    text = font.render(
        f"Gen: {generation}  Speed: {simulation_speed:.2f}  "
        f"Mode: {'TEST' if test_mode else 'TRAIN'}  "
        f"Best Apples: {best_apples}",
        True, (255,255,255))

    screen.blit(text, (10,10))
    pygame.display.update()


# ======================
# MAIN LOOP
# ======================

def run():
    global simulation_speed, test_mode
    global loaded_brain, loaded_generation

    generation = 1
    population = [Snake(NeuralNet()) for _ in range(POPULATION_SIZE)]

    while True:
        clock.tick(60)

        # --------- INPUT HANDLING ---------
        for event in pygame.event.get():

            if event.type == pygame.QUIT:
                pygame.quit()
                sys.exit()

            if event.type == pygame.KEYDOWN:

                mods = pygame.key.get_mods()
                shift_held = mods & pygame.KMOD_SHIFT
                speed_step = 10 if shift_held else 0.25

                if event.key == pygame.K_UP:
                    simulation_speed = min(MAX_SPEED, simulation_speed + speed_step)

                if event.key == pygame.K_DOWN:
                    simulation_speed = max(MIN_SPEED, simulation_speed - speed_step)

                if event.key == pygame.K_s:
                    save_best(population, generation)

                if event.key == pygame.K_l:
                    loaded = load_snake()
                    if loaded:
                        population = [loaded]
                        generation = loaded_generation
                        test_mode = True

                if event.key == pygame.K_t:
                    test_mode = not test_mode

                    if test_mode:
                        if loaded_brain:
                            population = [Snake(loaded_brain.clone())]
                            generation = loaded_generation
                    else:
                        if loaded_brain:
                            population = create_population_from_brain(loaded_brain)
                            generation = loaded_generation
                        else:
                            population = [Snake(NeuralNet()) for _ in range(POPULATION_SIZE)]
                            generation = 1

                if event.key == pygame.K_ESCAPE:
                    pygame.quit()
                    sys.exit()

        # --------- SIMULATION STEPS ---------
        steps = max(1, int(simulation_speed))

        for _ in range(steps):

            if test_mode:
                snake = population[0]
                if not snake.alive:
                    population[0] = Snake(snake.brain)
                population[0].update()

            else:
                if all(not s.alive for s in population):
                    population = evolve(population)
                    generation += 1
                    break

                for snake in population:
                    snake.update()

        draw(population, generation)


run()