const BASE_URL = "http://localhost:8080/Product";

//READ QUERY PARAMETERS FROM URL
const params = new URLSearchParams(window.location.search);
const type = params.get("type");

let endpoint=""
if(type==="name"){
    const name = params.get("name");
    endpoint=`${BASE_URL}/searchbyName?name=${encodeURIComponent(name)}`;
}
else if(type==="brand"){
    const brand=params.get("brand");
    endpoint=`${BASE_URL}/searchbyBrand?brand=${encodeURIComponent(brand)}`;
}
else if(type==="price"){
    const minprice=params.get("minPrice");
    const maxprice = params.get("maxPrice");

    endpoint=`${BASE_URL}/searchByPrice?minPrice=${minprice}&maxPrice=${maxprice}`;
}

async function loadSearchResults(){
    try{
        const response = await fetch(endpoint);

        if(!response.ok){
            throw new Error("failed to fetch results");
        }

        const products = await response.json();
        const tbody = document.querySelector("#resultsTable tbody");
        tbody.innerHTML = "";

        if(products.length===0){
            document.getElementById("resultsTable").style.display="none";
            document.getElementById("noResults").style.display="block";
            return;
        }
        products.forEach(prod =>{
            const row = document.createElement("tr");
            row.innerHTML = `
              <td>${prod.id}</td>
              <td>${prod.name}</td>
              <td>${prod.brand}</td>
              <td>${prod.price}</td>
              <td>${prod.description}</td> `;
              tbody.appendChild(row);

        });
    }catch(err){
        console.log(err);
        document.getElementById("resultsTable").style.display="none";
        const p  = document.getElementById("noResults");
        p.style.display = "block";
        p.textContent = "Error fetching details";
    }
}

loadSearchResults();
