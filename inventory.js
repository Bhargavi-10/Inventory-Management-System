if(!localStorage.getItem("loggedIn")){
    window.location.href = "login.html";
}

const role = localStorage.getItem("role");
if(role!="ADMIN"){
    document.getElementById("addProductBtn").style.display="none";
}

const BASE_URL = "http://localhost:8080/Inventory";

let weekchart = null;
let fullchart  =null;
let monthlyChart = null;
let fastSlowChart = null;
//load total Products
async function loadTotalProducts(){
    const response = await fetch("http://localhost:8080/Product/productCount");
    const count = await response.json();
    document.getElementById("totalProducts").innerText = count;
}

//load total inventory quantity
async function loadTotalInventory(){
    const response = await fetch(BASE_URL + "/inventoryQuantity");
    const total = await response.json();
    document.getElementById("totalInventoryQuantity").innerText = total;
}

//load low stock alerts
async function loadLowStockcount(){
    const response = await fetch(BASE_URL+"/lowstockproducts");
    const products = await response.json();
    document.getElementById("lowStockAlerts").innerText = products.length;
}

//load all inventory
async function loadInventory(){
    const response = await fetch(BASE_URL + "/productInventoryDTO");
    const inventory = await response.json();
    inventory.sort((a,b)=>a.name.toLowerCase().localeCompare(b.name.toLowerCase()));
    console.log(inventory);

    const tbody = document.querySelector("#inventoryTable tbody");
    tbody.innerHTML = "";

    inventory.forEach(item=>{
        const row = document.createElement("tr");
        if(item.quantity < item.reorderLevel){
            row.classList.add("low-stock");
        }
        row.innerHTML =` 
         <td>${item.id}</td>
         <td>${item.name}</td>
         <td>${item.brand}</td>
         <td>${item.price}</td>
         <td>${item.description}</td>
         <td>${item.quantity}</td>
         <td>${item.reorderLevel}</td>`;
   
         tbody.appendChild(row);

    });
}

//load recent movements

async function loadRecentMovements(){
    const response  = await fetch(BASE_URL+"/recentMovements");
    const movements = await response.json();

    const list = document.getElementById("movementList");
    list.innerHTML = "";

    if(!movements || movements.length==0){
        list.innerHTML = "<li class='empty'>No recent movements</li>";
        return;
    }
   
    movements.forEach(movement =>{
        const li = document.createElement("li");
        li.classList.add("movement-item");

        const formattedDate = new Date(movement.createdAt).toLocaleString();

        li.innerHTML = `
        <div class="movement-left">
            <span class = "movement-type ${movement.type==="IN"?"in":"out"}">
            ${movement.type}
            </span>
            <div class="movement-details">
                <strong>${movement.product.name}(ID: ${movement.product.id})</strong>
                <p>${movement.type==="IN"?"+":"-"}${movement.movedQuantity}(${movement.reason})</p>
            </div>
        </div>
        
        <div class="movement-time">
          ${formattedDate}
        </div>`
        list.appendChild(li);
    });
}

//initial load
loadTotalProducts();
loadTotalInventory();
loadLowStockcount();
loadInventory();
loadRecentMovements();
loadWeekMovement();


//functions to open and close forms
function openForm(formId){
    const form = document.getElementById(formId);

    document.getElementById(formId).style.display="block";

    //clear all input fields inside this form
    const inputs  = form.querySelectorAll("input");
    inputs.forEach(input =>{
        if(input.type!="button" && input.type!="submit"){
            input.value="";//reset value
        }
    })
}
function closeForm(formId){
    const form = document.getElementById(formId);
    document.getElementById(formId).style.display="none";

    const inputs = form.querySelectorAll("input");
    inputs.forEach(input =>{
        if(input.type!="button" && input.type!="submit"){
            input.value="";
        }
    });
}

//SHOW SECTIONS
function showSection(sectionId){
    document.querySelectorAll(".section").forEach(section=>{
        section.style.display="none";
    });
    const activeSection = document.getElementById(sectionId);
    if(activeSection){
        activeSection.style.display = "block";
    }

    if(sectionId ==="dashboard"){
        loadWeekMovement();

    }
    if(sectionId === "reports"){
        loadMovements();
        loadReportsData();
    }
    if(sectionId==="products"){
        loadInventory();
    }
    if(sectionId==="analytics"){
        loadMonthlySalesChart();
        loadFastSlowChart();
    }
}


function openUpdateForm(formId){
    const form = document.getElementById(formId);
    form.style.display="block";

    document.getElementById("updatePrice").value="";
    document.getElementById("updateDescription").value="";
    document.getElementById("updateQuantity").value="";
    document.getElementById("updateReorderLevel").value="";
}

async function refreshUI(){
    await loadInventory();
    await loadLowStockcount();
    await loadRecentMovements();
    await loadTotalProducts();
    await loadTotalInventory();
}

//ADD PRODUCT
document.getElementById("addProductForm").addEventListener("submit", async(e)=>{
    e.preventDefault();
    if(Number(document.getElementById("addPrice").value)<=0){
        alert("Invalid Price");
        return;
    }
    const product =  { //JS object
        name:document.getElementById("addName").value,
        brand:document.getElementById("addBrand").value,
        price:document.getElementById("addPrice").value,
        description:document.getElementById("addDescription").value
    };
    const quantity = document.getElementById("addQuantity").value;
    const reorderLevel = document.getElementById("addReorderLevel").value;

    if(Number(quantity)<0 || Number(reorderLevel)<0){
        alert("quantity and reorder level should be greater than 0!");
        return;
    }
    await fetch(`http://localhost:8080/Product/addPdt?quantitytoAdd=${quantity}&reorderLevel=${reorderLevel}`,{
        method:"POST",
        headers:{"content-type" : "application/json"}, //the product details are sent in the request body as JSON
        body: JSON.stringify(product)
    });
    alert("Product added Successfully");
    closeForm("addProductForm");
    await refreshUI();
});



//UPDATE PRODUCT 

//fetch product details
async function fetchProductDetails(){
    const Prodid = document.getElementById("updateId").value;
    if(!Prodid){
        alert("please enter product id");
        return;
    }

    const response = await fetch("http://localhost:8080/Product"+ `/getProductInventory/${Prodid}`);
   
    if(!response.ok){
        alert("product not found");
        return;
    }

    else{
        const data=await response.json();

       //Fill form with old data
        document.getElementById("updateId").value = data.id;
        document.getElementById("updateName").value =data.name;
        document.getElementById("updateBrand").value=data.brand;

        document.getElementById("updatePrice").value=data.price;
        document.getElementById("updateDescription").value=data.description;
        document.getElementById("updateReorderLevel").value=data.reorderLevel;
    }
}

//Update product Submit handler
document.getElementById("updateProductForm").addEventListener("submit" , async(e)=>{
    e.preventDefault();
    const Updateid = document.getElementById("updateId").value;
    const price = document.getElementById("updatePrice").value;
    const description= document.getElementById("updateDescription").value;
    const reorderLevel = document.getElementById("updateReorderLevel").value;
    if(reorderLevel<0 || price<=0){
        alert("reorder Level and price should be positive value");
        return;
    }
   if(!Updateid){
    alert("product ID missing");
    return;
   }
   await fetch("http://localhost:8080/Product" + `/update/${Updateid}?price=${price}&reorderLevel=${reorderLevel}&description=${description}`,   {method : "PUT"});
   closeForm("updateProductForm");
   alert("Product details updated successfully");
    await refreshUI();
});

//DELETE PRODUCT
document.getElementById("deleteProductForm").addEventListener("submit",async (e)=>{
   e.preventDefault();
   const deleteId= document.getElementById("DeleteID").value;
   if(deleteId<0){
    alert("invalid id!!");
    return;
   }

   await fetch("http://localhost:8080/Product"+`/delete/${deleteId}`,{method: "DELETE"});
   closeForm("deleteProductForm");
    await refreshUI();
});

//RESTOCK PRODUCT
document.getElementById("RestockProductForm").addEventListener("submit",async (e)=>{
    e.preventDefault();
    const Restockid= document.getElementById("RestockID").value;
    const quantity = document.getElementById("restockQuantity").value;
    if(Restockid<0 || quantity<0){
        alert("not valid!!");
        return;
    }
    await fetch(BASE_URL + `/restock?id=${Restockid}&addedQuantity=${quantity}`,{method : "PUT"});
    closeForm("RestockProductForm");
    await refreshUI();
})

//SELL PRODUCT
document.getElementById("SellProductForm").addEventListener("submit",async (e)=>{
    e.preventDefault();
    const id= document.getElementById("sellID").value;
    const sellquantity = document.getElementById("QuantitySold").value;
    const prodid = parseInt(id);
    const quantity = parseInt(sellquantity);
    if(!prodid || !quantity){
        alert("All fields are required");
        return;
    }
    if(prodid<0 ||quantity<0){
        alert("ID and Quantity should be greater than 0!");
        return;
    }
    try{
    const response = await fetch(`http://localhost:8080/Product/sell?id=${id}&quantitySold=${sellquantity}`,{
        method:"POST"
    });
     if(!response.ok){
        const errorMessage = await response.text();
        alert(errorMessage||"Error while selling Product!!");
        return;
     }
     alert("Product Sold Successfully!");
     closeForm("SellProductForm");
     await refreshUI();
    }catch(error){
        alert("server error! Please try again");
        console.log(error);
    }
});


//SEARCH FUNCTIONS
document.getElementById("searchByName").addEventListener("click" ,()=>{
    const name = document.getElementById("searchByNameinp").value;
    window.location.href = `search_results.html?type=name&name=${encodeURIComponent(name)}`;
});

document.getElementById("searchByBrand").addEventListener("click" , ()=>{
    const brand = document.getElementById("searchByBrandinp").value;
    window.location.href = `search_results.html?type=brand&brand=${encodeURIComponent(brand)}`;
});

document.getElementById("searchByPrice").addEventListener("click" , ()=>{
    const min = document.getElementById("minPriceinp").value;
    const max = document.getElementById("maxPriceinp").value;
    window.location.href = `search_results.html?type=price&minPrice=${min}&maxPrice=${max}` , "_blank";
});


//CLEAR SEARCH INPUTS
function clearSearchInputs(){
    document.getElementById("searchByNameinp").value="";
    document.getElementById("searchByBrandinp").value="";
    document.getElementById("minPriceinp").value="";
    document.getElementById("maxPriceinp").value="";
}

document.getElementById("clearSearchBtn").addEventListener("click" , ()=>{
    clearSearchInputs();
});


//WEEKLY MOVEMENT DASHBOARD
async function loadWeekMovement(){
    console.log("LOADING WEEKLY MOVEMENT...");
    const response = await fetch(BASE_URL + "/dashboard/movement");
    const data = await response.json();
    const labels = data.map(item=>item.date);
    const totalIn = data.map(item=>item.totalIn);
    const totalOut = data.map(item=>item.totalOut);

    const canvas = document.getElementById("weekMovementChart");

    //destroy old chart 
    if(weekchart){
        weekchart.destroy();
    }

    weekchart = new Chart(canvas , {
        type : "bar",
        data: {
            labels : labels,
            datasets:[
                {
                    label : "STOCK IN",
                    data : totalIn,
                    backgroundColor : "green"
                },
                {
                    label: "STOCK OUT",
                    data: totalOut,
                    backgroundColor: "red"
                }
            ]
        },

        options: {
            responsive: true,
            scales:{
                y:{beginAtZero : true}
            }
        }
    });
}

//FULL MOVEMENT IN STOCK REPORT
async function loadFullMovement(){
    const response = await fetch(BASE_URL + "/reports/movement");
    const data = await response.json();
    const labels = data.map(item=>item.date);
    const totalIn = data.map(item=>item.totalIn);
    const totalOut = data.map(item=>item.totalOut);

    const canvas = document.getElementById("fullMovementChart");
    if(!canvas) return;


    if(fullchart){
        fullchart.destroy();
    }
    fullchart = new Chart(canvas , {
        type : "bar",
        data: {
            labels : labels,
            datasets:[
                {
                    label : "STOCK IN",
                    data : totalIn,
                    backgroundColor : "green",
                    fill: false
                },
                {
                    label: "STOCK OUT",
                    data: totalOut,
                    backgroundColor: "red",
                    fill: false
                }
            ]
        },

        options: {
            responsive: true,
            scales:{
                y:{beginAtZero : true}
            }
        }
    });
}

//REPORTS 
async function loadReportsData(){
    //load full movement chart
    await loadFullMovement();

    //load low stock products
    const lowStockRes = await fetch(BASE_URL + "/lowstockproducts");
    const lowStock = await lowStockRes.json();

    document.getElementById("lowStockReport").innerText = lowStock.length;
    const lowStockTable = document.querySelector("#lowStockTable tbody");
    lowStockTable.innerHTML = "";

    lowStock.forEach(product =>{
        const row = document.createElement("tr");
        row.innerHTML = `
             <td>${product.id}</td>
             <td>${product.name}</td>
             <td>${product.brand}</td>
             <td>${product.quantity}</td>
             <td>${product.reorderLevel}</td>
           `;

           row.classList.add("low-stock");
           lowStockTable.appendChild(row);
    });

    //Load out of stock
    const outStockRes = await fetch(BASE_URL + "/outOfStock");
    const outStock = await outStockRes.json();
    document.getElementById("outStockReport").innerText = outStock.length;
    const outTable = document.querySelector("#outStockTable tbody");
    outTable.innerHTML = "";
        outStock.forEach(product =>{
        const row = document.createElement("tr");
        row.classList.add("out-of-stock");
        row.innerHTML = `
             <td>${product.id}</td>
             <td>${product.name}</td>
             <td>${product.brand}</td>
           `;
           outTable.appendChild(row);
    });


    //total movements count
    const movementRes = await fetch(BASE_URL + "/reports/movement");
    const movementData = await movementRes.json();
    let totalMovements=0;
    let totalin = 0;
    let totalOut =0;
    let totalInMovements = 0;
    let totalOutMovements=0;
    movementData.forEach(item=>{
        totalin+= item.totalIn;
        totalOut+=item.totalOut;
        totalInMovements+=item.totalInCount;
        totalOutMovements+=item.totalOutCount;
    })

    document.getElementById("days").innerText = movementData.length;
    document.getElementById("TotalIn").innerText=totalin;
    document.getElementById("TotalOut").innerText=totalOut;
    document.getElementById("incount").innerText=totalInMovements;
    document.getElementById("outcount").innerText=totalOutMovements;
    
}

async function loadMovements(){
    try{
        const response = await fetch("http://localhost:8080/Inventory/pdtmovements");
        const data = await response.json();
        const tableBody = document.querySelector("#movementTable tbody");
        tableBody.innerHTML ="";

        data.forEach(movement=>{
            const row = document.createElement("tr");
            row.innerHTML = `
            <td>${movement.product.name}</td>
            <td class="${movement.type==='IN'?'in':'out'}"> ${movement.type}</td>
            <td>${movement.movedQuantity}</td>
            <td>${movement.reason}</td>
            <td>${new Date(movement.createdAt).toLocaleString()}</td>`;

            tableBody.appendChild(row);
        });
    }catch(error){
       console.log("ERROR LOADING MOVEMENTS" , error);
    }
}

async function loadMonthlySalesChart(){
    const response = await fetch(BASE_URL + "/reports/monthly-sales");
    const data=await response.json();

    const months = [];
    const quantities = [];

    data.forEach(item=>{
        months.push(item[0]);
        quantities.push(Number(item[1]));
    });

    const context = document.getElementById("monthlySalesChart").getContext("2d");
    //destroy old chart  
    if(monthlyChart!==null){
        monthlyChart.destroy();
    }

    const formattedMonths = data.map(item=>{
        const date = new Date(item[0] + "-01");
        return date.toLocaleString("default",{month:"short",year:"numeric"})
    })
    monthlyChart = new Chart(context,{
        type:"line",
        data:{
            labels: formattedMonths,
            datasets:[{
                label:"Monthly Sales Quantity",
                data: quantities,
                borderColor: "#2b1e9e",          // 🔵 Line color
                backgroundColor: "rgba(108,92,231,0.2)", // Area color (optional)
                fill: false,
                tension: 0.3
            }]
        },
        options: {
            responsive: true,
            plugins:{
                legend:{
                    display: true
                }
            },
            scales:{
                x:{
                    title:{
                        display:true,
                        text:"MONTH"
                    }
                },
                y:{beginAtZero : true,
                    title:{
                        display:true,
                        text:"TOTAL QUANTITY SOLD"
                    }
                }
            }
        }
    });
}


async function loadFastSlowChart(){

    const response = await fetch(BASE_URL + "/reports/product-sales");
    const data=await response.json();

    const productNames = [];
    const quantities = [];

    data.forEach(item=>{
        productNames.push(item[0]);
        quantities.push(Number(item[1]));
    });

    const backgroundColors = [];
    const borderColors=[];
    const minIndex = Math.floor(quantities.length/2);

    quantities.forEach((value,index)=>{
        if(index<minIndex){
        backgroundColors.push("#FF6F00");  // Teal (Fast)
        borderColors.push("#00B894");
        }
        else {
        // 🟣 Slow Moving
        backgroundColors.push("#00C2FF"); // Orange (Slow)
        borderColors.push("#D4A5A5");
        }
    })

    const context = document.getElementById("speedChart").getContext("2d");
    //destroy old chart  
    if(fastSlowChart!==null){
        fastSlowChart.destroy();
    }
    fastSlowChart = new Chart(context,{
        type:"bar",
        data:{
            labels: productNames,
            datasets:[{
                label:"Fast Moving",
                data: quantities.map((value,index)=>
                    index<minIndex?value:null
                ),
                backgroundColor: "#FF6F00",
                borderColor:"#FF6F00",
                borderWidth: 1

            },
            {
                label:"Slow Moving",
                data: quantities.map((value,index)=>
                    index>=minIndex?value:null
                ),
                backgroundColor:"#00C2FF",
                borderColor:"#00C2FF",
                borderWidth: 1
            }  
              ]
        },
        options: {
            responsive: true,
            plugins:{
                legend:{
                    display: true
                }
            },            
            scales:{
                x:{
                    title:{
                        display:true,
                        text : "PRODUCTS"
                    }
                },
                y:{
                    beginAtZero : true,
                    title:{
                        display:true,
                        text:"QUANTITY SOLD"
                    }
                }
            }
        }
    });  
}

function logout(){
    localStorage.removeItem("loggedIn");
    window.location.href="login.html";
}

