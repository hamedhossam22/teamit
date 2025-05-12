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
document.addEventListener("DOMContentLoaded", () => {
  // ========== Register ==========
  const registerForm = document.getElementById("registerForm");

  if (registerForm) {
    registerForm.addEventListener("submit", function (e) {
      e.preventDefault();

      const name = registerForm.querySelector('input[placeholder="Full Name"]').value.trim();
      const email = registerForm.querySelector('input[placeholder="Email"]').value.trim();
      const password = registerForm.querySelectorAll('input[placeholder="Password"]')[0].value;
      const confirmPassword = registerForm.querySelector('input[placeholder="Confirm Password"]').value;

      if (!name || !email || !password || !confirmPassword) {
        alert("Please fill in all fields.");
        return;
      }

      if (password !== confirmPassword) {
        alert("Passwords do not match.");
        return;
      }

      localStorage.setItem("user", JSON.stringify({ name, email, password }));
      alert("Registration successful!");
      window.location.href = "login.html";
    });
  }

  // ========== Login ==========
  const loginForm = document.getElementById("loginForm");

  if (loginForm) {
    loginForm.addEventListener("submit", function (e) {
      e.preventDefault();

      const email = loginForm.querySelector('input[placeholder="Email"]').value.trim();
      const password = loginForm.querySelector('input[placeholder="Password"]').value;

      const storedUser = JSON.parse(localStorage.getItem("user"));

      if (!storedUser || storedUser.email !== email || storedUser.password !== password) {
        alert("Invalid email or password.");
        return;
      }

      alert(`Welcome back, ${storedUser.name}!`);
      window.location.href = "home.html"; // عدل المسار حسب اسم صفحة الهوم عندك
    });
  }
});
const recipes = {
  eggsBenedict: {
    name: "Eggs Benedict",
    ingredients: "Eggs, English Muffin, Ham, Hollandaise Sauce",
    nutrition: "250 kcal",
    cookingTime: "20 mins"
  },
  avocadoToast: {
    name: "Avocado Toast",
    ingredients: "Avocado, Bread, Olive Oil, Salt, Lemon",
    nutrition: "180 kcal",
    cookingTime: "10 mins"
  },
  frenchToast: {
    name: "French Toast",
    ingredients: "Bread, Eggs, Milk, Sugar, Cinnamon",
    nutrition: "290 kcal",
    cookingTime: "15 mins"
  },
  lemonPancakes: {
    name: "Lemon Pancakes",
    ingredients: "Flour, Eggs, Milk, Lemon Zest, Sugar",
    nutrition: "300 kcal",
    cookingTime: "20 mins"
  },
  cheesyOmelette: {
    name: "Cheesy Omelette",
    ingredients: "Eggs, Cheese, Milk, Butter",
    nutrition: "200 kcal",
    cookingTime: "8 mins"
  },
  granolaYogurt: {
    name: "Granola Yogurt",
    ingredients: "Granola, Greek Yogurt, Honey, Berries",
    nutrition: "220 kcal",
    cookingTime: "5 mins"
  },
  burrito: {
    name: "Breakfast Burrito",
    ingredients: "Tortilla, Eggs, Beans, Cheese, Salsa",
    nutrition: "350 kcal",
    cookingTime: "15 mins"
  },
  smoothieBowl: {
    name: "Smoothie Bowl",
    ingredients: "Banana, Berries, Yogurt, Granola, Honey",
    nutrition: "180 kcal",
    cookingTime: "7 mins"
  }
};

function compareSelected() {
  const checkboxes = document.querySelectorAll("input[type='checkbox']:checked");
  const comparisonDiv = document.getElementById("comparisonTable");

  // If less than two recipes are selected, hide the comparison table
  if (checkboxes.length < 2) {
    comparisonDiv.classList.add("hidden");
    return;
  }

  let tableHTML = `
    <table>
      <tr>
        <th>Recipe</th>
        ${Array.from(checkboxes).map(cb => `<td>${recipes[cb.value].name}</td>`).join("")}
      </tr>
      <tr>
        <th>Ingredients</th>
        ${Array.from(checkboxes).map(cb => `<td>${recipes[cb.value].ingredients}</td>`).join("")}
      </tr>
      <tr>
        <th>Nutrition</th>
        ${Array.from(checkboxes).map(cb => `<td>${recipes[cb.value].nutrition}</td>`).join("")}
      </tr>
      <tr>
        <th>Cooking Time</th>
        ${Array.from(checkboxes).map(cb => `<td>${recipes[cb.value].cookingTime}</td>`).join("")}
      </tr>
    </table>
  `;

  comparisonDiv.innerHTML = tableHTML;
  comparisonDiv.classList.remove("hidden");
}



// عند تحميل الصفحة، اعرض الخطة المحفوظة (لو موجودة)
document.addEventListener('DOMContentLoaded', () => {
    const saved = localStorage.getItem("mealPlan");
    if (saved) {
        displaySavedMealPlan(JSON.parse(saved));
    }
});

document.querySelector('.save-btn').addEventListener('click', saveMealPlan);

function saveMealPlan() {
    const mealPlan = [];
    const inputs = document.querySelectorAll('.meal-input');
    for (let i = 0; i < inputs.length; i += 3) {
        const dayMeals = [
            inputs[i].value,      // Breakfast
            inputs[i + 1].value,  // Lunch
            inputs[i + 2].value   // Dinner
        ];
        mealPlan.push(dayMeals.join(','));
    }

    localStorage.setItem("mealPlan", JSON.stringify(mealPlan));
    displaySavedMealPlan(mealPlan);
}

function displaySavedMealPlan(mealPlan) {
    // إزالة القسم القديم إن وجد
    const oldSection = document.querySelector('.saved-plan');
    if (oldSection) oldSection.remove();

    // إنشاء القسم الجديد
    const displaySection = document.createElement('section');
    displaySection.classList.add('saved-plan');
    const heading = document.createElement('h3');
    heading.textContent = 'Saved Meal Plan';
    displaySection.appendChild(heading);

    const list = document.createElement('ul');
    mealPlan.forEach((day, index) => {
        const dayMeals = day.split(',');
        const listItem = document.createElement('li');
        listItem.textContent = `Day ${index + 1}: Breakfast - ${dayMeals[0]}, Lunch - ${dayMeals[1]}, Dinner - ${dayMeals[2]}`;
        list.appendChild(listItem);
    });

    displaySection.appendChild(list);
    document.querySelector('main').appendChild(displaySection);
}

