import pandas as pd
import matplotlib.pyplot as plt

# Read the CSV file
df = pd.read_csv('/home/hayden/code/csci312/vacuum-agents/square_envs_results.csv')

# Create a dictionary to map agent types to colors
agent_colors = {
    'HahRandomAgent': 'blue',
    'HahVacuumModelReflexBumpAgent': 'red'
}

# Create a 1x2 grid of subplots
fig, axes = plt.subplots(nrows=1, ncols=2, figsize=(14, 6))

# Subplot 1: Score vs. Environment Size
axes[0].set_xlabel('Environment Size n x n')
axes[0].set_ylabel('Score')

# Subplot 2: Time Steps vs. Environment Size
axes[1].set_xlabel('Environment Size n x n')
axes[1].set_ylabel('Time Steps')

for agent_type, color in agent_colors.items():
    agent_data = df[df['agent'] == agent_type]
    
    # Subplot 1: Scatter plot for Score vs. Environment Size
    axes[0].scatter(agent_data['environment'], agent_data['score'], label=agent_type, color=color)
    axes[0].set_title('Score vs. Environment Size')
    
    # Subplot 2: Scatter plot for Time Steps vs. Environment Size
    axes[1].scatter(agent_data['environment'], agent_data['time_steps'], label=agent_type, color=color)
    axes[1].set_title('Time Steps vs. Environment Size')

# Add legends and grids to both subplots
for ax in axes:
    ax.legend()
    ax.grid(True)

plt.suptitle("Agent Performance in Square Environments")

# Adjust layout and show the plot or save it to a file
plt.tight_layout()
plt.show()

