// Highlight active nav link based on current page
const navLinks = document.querySelectorAll('nav a');
navLinks.forEach(link => {
  if (link.href === window.location.href || link.href === window.location.origin + window.location.pathname) {
    link.classList.add('active');
  }
});

// Redirect user to the recipe details page on card click
document.querySelectorAll('.trending-card').forEach(card => {
  card.addEventListener('click', function() {
    const recipeId = this.dataset.recipeId;  // Get recipe ID from data attribute
    window.location.href = `recipe-detail.html?id=${recipeId}`;
  });
});

// Save recipe to localStorage and update the save count
document.querySelectorAll('.save-btn').forEach(button => {
  button.addEventListener('click', function(e) {
    e.stopPropagation();  // Prevent redirect when clicking save button
    
    const recipeCard = this.closest('.recipe-card');  // Find closest recipe card
    const recipeId = recipeCard.dataset.recipeId;  // Get recipe ID
    let saveCount = parseInt(recipeCard.querySelector('.save-count span').innerText);

    // Toggle saved state and update count
    if (localStorage.getItem(recipeId)) {
      localStorage.removeItem(recipeId);
      saveCount -= 1;
    } else {
      localStorage.setItem(recipeId, 'saved');
      saveCount += 1;
    }
    
    // Update the save count in the UI
    recipeCard.querySelector('.save-count span').innerText = saveCount;
    this.querySelector('i').classList.toggle('far');  // Toggle heart icon
    this.querySelector('i').classList.toggle('fas');  // Toggle filled heart icon
  });
});

// Initialize save buttons based on localStorage
document.querySelectorAll('.recipe-card').forEach(card => {
  const recipeId = card.dataset.recipeId;
  if (localStorage.getItem(recipeId)) {
    const saveButton = card.querySelector('.save-btn');
    saveButton.querySelector('i').classList.add('fas');  // Set heart icon as filled
    card.querySelector('.save-count span').innerText = parseInt(card.querySelector('.save-count span').innerText) + 1;
  }
});
