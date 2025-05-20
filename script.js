const navLinks = document.querySelectorAll('nav a');
navLinks.forEach(link => {
  if (link.href === window.location.href || link.href === window.location.origin + window.location.pathname) {
    link.classList.add('active');
  }
});



document.querySelectorAll('.save-btn').forEach(button => {
  button.addEventListener('click', function(e) {
    e.stopPropagation();  
    
    const recipeCard = this.closest('.recipe-card');  
    const recipeId = recipeCard.dataset.recipeId;  
    let saveCount = parseInt(recipeCard.querySelector('.save-count span').innerText);

    if (localStorage.getItem(recipeId)) {
      localStorage.removeItem(recipeId);
      saveCount -= 1;
    } else {
      localStorage.setItem(recipeId, 'saved');
      saveCount += 1;
    }
    
    recipeCard.querySelector('.save-count span').innerText = saveCount;
    this.querySelector('i').classList.toggle('far');  
    this.querySelector('i').classList.toggle('fas');  
  });
});

document.querySelectorAll('.recipe-card').forEach(card => {
  const recipeId = card.dataset.recipeId;
  if (localStorage.getItem(recipeId)) {
    const saveButton = card.querySelector('.save-btn');
    saveButton.querySelector('i').classList.add('fas');  
    card.querySelector('.save-count span').innerText = parseInt(card.querySelector('.save-count span').innerText) + 1;
  }
});


// حماية الصفحة
const isLoggedIn = localStorage.getItem("isLoggedIn");

if (!isLoggedIn || isLoggedIn !== "true") {
  window.location.href = "login.html";
}

function logout() {
  localStorage.removeItem("isLoggedIn");
  window.location.href = "login.html";
}

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



