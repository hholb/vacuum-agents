import pandas as pd
import matplotlib.pyplot as plt

# Read the CSV file
df = pd.read_csv('/home/hayden/code/csci312/vacuum-agents/nonsquare_envs_comparison_results.csv')

# Create a dictionary to map agent types to colors
agent_colors = {
    'TPVacuumModelReflexLocAgent': 'blue',
    'HahVacuumModelReflexBumpAgent': 'red'
}

# Extract file name from path (unix style) for the environment names
df['environment'] = df['environment'].str.split('/').str[-1]

# Create a 1x2 grid of subplots
fig, axes = plt.subplots(nrows=1, ncols=2, figsize=(14, 6))

# Subplot 1: Score vs. Environment
axes[0].set_xlabel('Environment')
axes[0].set_ylabel('Score')

# Subplot 2: Time Steps vs. Environment
axes[1].set_xlabel('Environment')
axes[1].set_ylabel('Time Steps')

for agent_type, color in agent_colors.items():
    agent_data = df[df['agent'] == agent_type]
    
    # Subplot 1: Scatter plot for Score vs. Environment
    axes[0].scatter(agent_data['environment'], agent_data['score'], label=agent_type, color=color)
    axes[0].set_title('Score vs. Environment')
    
    # Subplot 2: Scatter plot for Time Steps vs. Environment
    axes[1].scatter(agent_data['environment'], agent_data['time_steps'], label=agent_type, color=color)
    axes[1].set_title('Time Steps vs. Environment')

# Add legends and grids to both subplots
for ax in axes:
    ax.legend()
    ax.grid(True)

# Add a title to the entire figure
plt.suptitle('Comparison of Agent Performance In Non-Square Environments')

# Rotate x-axis labels for better readability
for ax in axes:
    plt.sca(ax)
    plt.xticks(rotation=45, ha='right')

# Adjust layout and show the plot or save it to a file
plt.tight_layout()
plt.show()

